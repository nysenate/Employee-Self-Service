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
#

prog=`basename $0`
tmpfile=ess_batch_contact_export_$$.xml
timestamp=`date +%Y%m%d%H%M%S`
swnfile=writing_request_$timestamp.xml

usage() {
  echo "Usage: $prog [--config-file file] [--tmpdir dir] [--keep-tmpfile] [--xmlfile file] [--no-xml | --no-ftp] [--verbose]" >&2
}

cfgfile=/etc/sendwordnow.cfg
tmpdir=/tmp
keep_tmpfile=0
xml_file=
no_xml=0
no_ftp=0
verbose=0

while [ $# -gt 0 ]; do
  case "$1" in
    --conf*|-c) shift; cfgfile="$1" ;;
    --tmpdir|-t) shift; tmpdir="$1" ;;
    --keep*|-k) keep_tmpfile=1 ;;
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

echo "Reading configuration file [$cfgfile]"
. "$cfgfile"

if [ "$esshost" -a "$swnhost" -a "$swnuser" -a "$swnpass" ]; then
  echo "Retrieved values for esshost, swnhost, swnuser, and swnpass"
else
  echo "$prog: $cfgfile: Must specify values for esshost, swnhost, swnuser, and swnpass" >&2
  exit 1
fi

lftp_cmds="mrm *.xml; put $tmpdir/$tmpfile; mv $tmpfile $swnfile; exit"
lftp_mode="file transfer"

if [ $no_xml -eq 1 ]; then
  echo "Skipping the XML export from ESS; no XML will be uploaded to SWN"
  lftp_cmds=
  lftp_mode="interactive"
elif [ "$xml_file" ]; then
  echo "Using the provided XML file [$xml_file]"
  cat "$xml_file" >"$tmpdir/$tmpfile"
else
  echo "Requesting an XML export of the contact data from ESS"
  essApi.sh eax --no-auth --host "$esshost" >"$tmpdir/$tmpfile" 2>/dev/null || exit 1

  if [ ! -r "$tmpdir/$tmpfile" ]; then
    echo "$prog: $tmpfile: ESS host [$esshost] did not export any XML data" >&2
    exit 1
  elif cut -c1-13 "$tmpdir/$tmpfile" | grep -q -v '<contactBatch'; then
    echo "$prog: $tmpfile: File does not start with XML <contactBatch> tag" >&2
    [ $keep_tmpfile -eq 1 ] || rm -rf "$tmpdir/$tmpfile"
    exit 1
  fi
fi

if [ $no_ftp -eq 1 ]; then
  echo "Skipping the SFTP connection to SendWordNow in $lftp_mode mode"
else
  echo "Connecting to SendWordNow in $lftp_mode mode"
  lftp sftp://"$swnuser":"$swnpass"@"$swnhost" -e "set sftp:auto-confirm y; set sftp:connect-program 'ssh -a -x -oHostKeyAlgorithms=+ssh-dss'; $lftp_cmds"
fi

[ $keep_tmpfile -eq 1 ] || rm -f "$tmpdir/$tmpfile"
exit 0
