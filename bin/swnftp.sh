#!/bin/bash
#
# swnftp.sh - Send Senate contact data in bulk to SendWordNow
#
# Organization: New York State Senate
# Project: ESS/Alert
# Author: Ken Zalewski
# Date: 2017-08-16
# Revised: 2017-08-29 - added configuration file /etc/sendwordnow.cfg
# Revised: 2017-08-30 - added --no-xml and --no-ftp options
# Revised: 2017-12-20 - added better logging and error checking
# Revised: 2018-01-11 - moved essApi.sh here; esshost removed from swn config
# Revised: 2018-01-12 - trap and display errors from essApi.sh
#

prog=`basename $0`
script_dir=`dirname $0`
tmpfile=ess_batch_contact_export_$$.xml
timestamp=`date +%Y%m%d%H%M%S`
swnfile=writing_request_$timestamp.xml

usage() {
  echo "Usage: $prog [--config-file file] [--tmpdir dir] [--keep-tmpfile] [--pretty] [--xmlfile file] [--no-xml | --no-ftp] [--verbose]" >&2
}

logdt() {
  echo "[`date +%Y-%m-%d\ %H:%M:%S`] $@"
}


cfgfile=/etc/sendwordnow.cfg
tmpdir=/tmp
keep_tmpfile=0
xml_filter=cat
xml_file=
no_xml=0
no_ftp=0
verbose=0

while [ $# -gt 0 ]; do
  case "$1" in
    --conf*|-c) shift; cfgfile="$1" ;;
    --tmpdir|-t) shift; tmpdir="$1" ;;
    --keep*|-k) keep_tmpfile=1 ;;
    --pretty|-p) xml_filter="xmllint --format - " ;;
    --xml*|-x) shift; xml_file="$1" ;;
    --no-xml|-n) no_xml=1 ;;
    --no-ftp|-N) no_ftp=1 ;;
    --verbose|-v) set -x ;;
    --help|-h) usage; exit 0 ;;
    *) echo "$prog: $1: Invalid option" >&2; exit 1 ;;
  esac
  shift
done

if [ ! "$cfgfile" ]; then
  echo "$prog: A SendWordNow configuration file must be specified" >&2
  exit 1
elif [ ! -r "$cfgfile" ]; then
  echo "$prog: $cfgfile: SendWordNow configuration file cannot be read" >&2
  exit 1
elif [ ! "$tmpdir" ]; then
  echo "$prog: A temporary directory must be specified" >&2
  exit 1
elif [ ! -w "$tmpdir" ]; then
  echo "$prog: $tmpdir: Temp directory is not writable" >&2
  exit 1
elif [ "$xml_file" -a $no_xml -eq 1 ]; then
  echo "$prog: --xml-file and --no-xml cannot both be specified" >&2
  exit 1
elif [ $no_xml -eq 1 -a $no_ftp -eq 1 ]; then
  echo "$prog: Warning: Using both --no-xml and --no-ftp leaves this script with almost nothing to do" >&2
elif [ "$xml_file" -a ! -r "$xml_file" ]; then
  echo "$prog: $xml_file: File not found" >&2
  exit 1
fi

logdt "About to transfer ESS/Alert contact data to SendWordNow"

logdt "Reading configuration file [$cfgfile]"
. "$cfgfile"

if [ "$swnhost" -a "$swnuser" -a "$swnpass" ]; then
  logdt "Retrieved values for swnhost, swnuser, and swnpass"
else
  echo "$prog: $cfgfile: Must specify values for swnhost, swnuser, and swnpass" >&2
  exit 1
fi

lftp_cmds="mrm *.xml; put $tmpdir/$tmpfile; mv $tmpfile $swnfile; exit"
lftp_mode="file transfer"

if [ $no_xml -eq 1 ]; then
  logdt "Skipping the XML export from ESS; no XML will be uploaded to SWN"
  lftp_cmds=
  lftp_mode="interactive"
elif [ "$xml_file" ]; then
  logdt "Using the provided XML file [$xml_file]"
  cat "$xml_file" | $xml_filter >"$tmpdir/$tmpfile"
else
  logdt "Requesting an XML export of the contact data from ESS"
  set -o pipefail
  $script_dir/essApi.sh eax --no-auth | $xml_filter >"$tmpdir/$tmpfile"

  if [ $? -ne 0 -o ! -r "$tmpdir/$tmpfile" ]; then
    echo "$prog: $tmpfile: Failed to write the XML dump file" >&2
    rm -f "$tmpdir/$tmpfile"
    exit 1
  elif head -c 13 "$tmpdir/$tmpfile" | egrep -q '^(<contactBatch|<[?]xml)'; then
    :
  else
    echo "$prog: $tmpfile: File does not start with <?xml> or <contactBatch> tags" >&2
    [ $keep_tmpfile -eq 1 ] || rm -rf "$tmpdir/$tmpfile"
    exit 1
  fi
fi

if [ $no_ftp -eq 1 ]; then
  logdt "Skipping the SFTP connection to SendWordNow in $lftp_mode mode"
else
  logdt "Connecting to SendWordNow in $lftp_mode mode"
  lftp sftp://"$swnuser":"$swnpass"@"$swnhost" -e "set sftp:auto-confirm y; set sftp:connect-program 'ssh -a -x -oHostKeyAlgorithms=+ssh-dss'; $lftp_cmds"
fi

[ $keep_tmpfile -eq 1 ] || rm -f "$tmpdir/$tmpfile"

logdt "Finished transferring ESS/Alert contact data to SendWordNow"
exit 0
