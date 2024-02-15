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
# Revised: 2024-01-04 - changed --no-xml to --no-export
#                     - export as CSV instead of XML by default
#                     - add --xml option to force XML; remove --pretty
#                     - authenticate using SSH keys
#

prog=`basename $0`
script_dir=`dirname $0`
#swnfilebase=nysenate_onsolve_`date +%Y%m%d`
swnfilebase=nysenate_onsolve
tmpfile=ess_batch_contact_export_$$.tmp

usage() {
  echo "Usage: $prog [--config-file file] [--export-file file] [--keep-tmpfile] [--no-export | --no-ftp] [--swnfilename filename] [--tmpdir dir] [--verbose] [--xml]" >&2
}

logdt() {
  echo "[`date +%Y-%m-%d\ %H:%M:%S`] $@"
}


cfgfile=/etc/sendwordnow.cfg
export_file=
export_file_filter=cat
export_format=csv
export_pattern_check='^Source Key,'
keep_tmpfile=0
no_export=0
no_ftp=0
swnfilename=
tmpdir=/tmp
verbose=0

while [ $# -gt 0 ]; do
  case "$1" in
    --conf*|-c) shift; cfgfile="$1" ;;
    --export*|-f) shift; export_file="$1" ;;
    --keep*|-k) keep_tmpfile=1 ;;
    --no-export|-n) no_export=1 ;;
    --no-ftp|-N) no_ftp=1 ;;
    --swn*) shift; swnfilename="$1" ;;
    --tmpdir|-t) shift; tmpdir="$1" ;;
    --verbose|-v) set -x ;;
    --xml|-x) export_format=xml; export_file_filter="xmllint --format - "; export_pattern_check='^(<contactBatch|<[?]xml)' ;;
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
elif [ "$export_file" -a $no_export -eq 1 ]; then
  echo "$prog: --export-file and --no-export cannot both be specified" >&2
  exit 1
elif [ $no_export -eq 1 -a $no_ftp -eq 1 ]; then
  echo "$prog: Warning: Using both --no-export and --no-ftp leaves this script with almost nothing to do" >&2
elif [ "$export_file" -a ! -r "$export_file" ]; then
  echo "$prog: $export_file: File not found" >&2
  exit 1
elif [ ! "$swnfilename" ]; then
  swnfilename="$swnfilebase.$export_format"
fi

logdt "About to transfer ESS/Alert contact data to SendWordNow"

swnhost=
swnuser=
swnpass=
swndir=.

logdt "Reading configuration file [$cfgfile]"
. "$cfgfile"

if [ "$swnhost" -a "$swnuser" ]; then
  logdt "Retrieved mandatory values for swnhost and swnuser"
else
  echo "$prog: $cfgfile: Must specify values for swnhost and swnuser" >&2
  exit 1
fi

lftp_cmds="cd $swndir; put $tmpdir/$tmpfile; rm $swnfilename; mv $tmpfile $swnfilename; exit"
lftp_mode="file transfer"

if [ $no_export -eq 1 ]; then
  logdt "Skipping the contact export from ESS; no file will be uploaded to SWN"
  lftp_cmds=
  lftp_mode="interactive"
elif [ "$export_file" ]; then
  logdt "Using the provided contact export file [$export_file]"
  cat "$export_file" | $export_file_filter >"$tmpdir/$tmpfile"
else
  logdt "Requesting an export [$export_format] of the contact data from ESS"
  set -o pipefail
  $script_dir/essApi.sh eax --$export_format | $export_file_filter >"$tmpdir/$tmpfile"

  if [ $? -ne 0 -o ! -r "$tmpdir/$tmpfile" ]; then
    echo "$prog: $tmpfile: Failed to write the $export_format dump file" >&2
    rm -f "$tmpdir/$tmpfile"
    exit 1
  elif head -c 13 "$tmpdir/$tmpfile" | egrep -q "$export_pattern_check"; then
    :
  else
    echo "$prog: $tmpfile: File does not start with expected $export_format header" >&2
    [ $keep_tmpfile -eq 1 ] || rm -rf "$tmpdir/$tmpfile"
    exit 1
  fi
fi

if [ $no_ftp -eq 1 ]; then
  logdt "Skipping the SFTP connection to SendWordNow in $lftp_mode mode"
else
  logdt "Connecting to SendWordNow in $lftp_mode mode"
  lftp -u "$swnuser,$swnpass" sftp://"$swnhost" -e "set sftp:auto-confirm y; $lftp_cmds"
fi

[ $keep_tmpfile -eq 1 ] || rm -f "$tmpdir/$tmpfile"

logdt "Finished transferring ESS/Alert contact data to SendWordNow"
exit 0
