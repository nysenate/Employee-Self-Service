#!/bin/sh
#
# essApi.sh - Call certain ESS API methods from the shell
#
# Organization: New York State Senate
# Project: ESS
# Author: Ken Zalewski
# Date: 2017-06-05
# Revised: 2018-01-09 - refactor for inclusion in ESS project
# Revised: 2018-01-11 - add all of the cache-clear commands
#

prog=`basename $0`

usage() {
  echo "Usage: $prog [--config-file|-c config_file] [--host|-h hostname] [--user|-u username] [--pass|-p password] [--no-auth | -n] [--verbose | -v] { trm | cc-[all|aa|atr|emp|hol|loc|pp|seg|txn] | eax }" >&2
  echo "where:" >&2
  echo "  trm    = run the Time Record Manager" >&2
  echo "  cc-all = clear all ESS/Time caches" >&2
  echo "  cc-aa  = clear the Annual Accrual cache" >&2
  echo "  cc-atr = clear the Active Time Record cache" >&2
  echo "  cc-emp = clear the Employee cache" >&2
  echo "  cc-hol = clear the Holiday cache" >&2
  echo "  cc-loc = clear the Location cache" >&2
  echo "  cc-pp  = clear the Pay Period cache" >&2
  echo "  cc-seg = clear the Supervisor Emp Group cache" >&2
  echo "  cc-txn = clear the Transaction cache" >&2
  echo "  eax    = dump the ESS/Alert XML feed" >&2
  echo "" >&2
  echo "Config file can have three parameters:  host, user, pass" >&2
}

if [ $# -lt 1 ]; then
  usage
  exit 1
fi

cfgfile=/etc/essApi.cfg
cookietmp=/tmp/ess_cookie_$$.tmp
esshost=
essuser=
esspass=
no_auth=0

while [ $# -gt 0 ]; do
  case "$1" in
    --conf*|-c) shift; cfgfile="$1" ;;
    --host|-h) shift; esshost="$1" ;;
    --user|-u) shift; essuser="$1" ;;
    --pass|-p) shift; esspass="$1" ;;
    --no-auth|-n) no_auth=1 ;;
    --verbose|-v) set -x ;;
    -*) echo "$prog: $1: Invalid option" >&2; usage; exit 1 ;;
    *) cmd="$1" ;;
  esac
  shift
done

if [ -r "$cfgfile" ]; then
  . "$cfgfile"
  [ ! "$esshost" -a "$host" ] && esshost="$host"
  [ ! "$essuser" -a "$user" ] && essuser="$user"
  [ ! "$esspass" -a "$pass" ] && esspass="$pass"
else
  echo "$prog: Warning: Config file [$cfgfile] not found" >&2
fi

if [ ! "$esshost" ]; then
  echo "$prog: ESS hostname must be specified using either --host command line option or host= config parameter" >&2
  exit 1
elif [ $no_auth -ne 1 -a ! "$essuser" ]; then
  echo "$prog: ESS username must be specified using either --user command line option or user= config parameter" >&2
  exit 1
fi

if echo "$esshost" | grep -q '\.'; then
  :
else
  esshost="$esshost.nysenate.gov"
fi

base_url="https://$esshost:8443"
base_api_url="$base_url/api/v1"

case "$cmd" in
  trm) http_req=POST; url="admin/time/timerecords/manager" ;;
  cc-*)
    http_req=DELETE
    subcmd=`echo $cmd | cut -d"-" -f2`
    case "$subcmd" in
      all) cache="ALL" ;;
      aa)  cache="ACCRUAL_ANNUAL" ;;
      atr) cache="ACTIVE_TIME_RECORDS" ;;
      emp) cache="EMPLOYEE" ;;
      hol) cache="HOLIDAY" ;;
      loc) cache="LOCATION" ;;
      pp)  cache="PAY_PERIOD" ;;
      seg) cache="SUPERVISOR_EMP_GROUP" ;;
      txn) cache="TRANSACTION" ;;
      *) echo "$prog: $cmd: Unknown clear-cache command" >&2; usage; exit 1 ;;
    esac
    url="admin/cache/$cache"
    ;;
  eax) http_req=GET; url="alert-info/contact-dump.xml" ;;
  *) echo "$prog: $cmd: Unknown API command" >&2; usage; exit 1 ;;
esac


if [ $no_auth -eq 1 ]; then
  curl -X $http_req -v "$base_api_url/$url" -H 'Accept:application/json'
else
  [ "$esspass" ] || read -s -p "Password: " esspass

  curl -X POST -v -c "$cookietmp" "$base_url/login" -H 'Accept:application/json' -H 'Content-Type:application/x-www-form-urlencoded' -d "username=$essuser&password=$esspass&rememberMe=false"
  curl -X $http_req -v -b "$cookietmp" "$base_api_url/$url" -H 'Accept:application/json'
  rm -f "$cookietmp"
fi

exit $?
