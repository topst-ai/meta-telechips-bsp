#! /bin/sh
### BEGIN INIT INFO
# Provides:             Telechips USB Manager
# Required-Start:
# Required-Stop:
# Default-Start:        2 5
# Default-Stop:         0
# Short-Description:    Telechips USB Manager
# Description:          umd is a usb recovery manager for Telechips USB
### END INIT INFO
#
# -*- coding: utf-8 -*-
# Debian init.d script for Telechips USB Manager
# Copyright © 2014 Wily Taekhyun Shin <thshin@telechips.com>

# Source function library.
. /etc/init.d/functions

# /etc/init.d/tc-usb-manager: start and stop the telechips usb manager daemon

DAEMON=/usr/bin/umd
RUNDIR=/var/run/umd
DESC="telechips usb manager"
ARGUMENTS=""

test -x $DAEMON || exit 0

[ -z "$SYSCONFDIR" ] && SYSCONFDIR=/var/lib/umd
mkdir -p $SYSCONFDIR

check_for_no_start() {
    # forget it if we're trying to start, and /var/lib/umd/umd_not_to_be_run exists
    if [ -e $SYSCONFDIR/umd_not_to_be_run ]; then
	echo "USB Manager not in use ($SYSCONFDIR/umd_not_to_be_run)"
	exit 0
    fi
}

check_privsep_dir() {
    # Create the PrivSep empty dir if necessary
    if [ ! -d $RUNDIR ]; then
		mkdir -p $RUNDIR
    fi
}

case "$1" in
  start)
	check_for_no_start
  	echo -n "Starting $DESC: "
	check_privsep_dir
	start-stop-daemon -S -x $DAEMON -- $ARGUMENTS
  	echo "done."
	;;
  stop)
  	echo -n "Stopping $DESC: "
	start-stop-daemon -K -x $DAEMON
  	echo "done."
	;;

  restart)
  	echo -n "Restarting $DESC: "
	start-stop-daemon -K --oknodo -x $DAEMON
	check_for_no_start
	check_privsep_dir
	sleep 1
	start-stop-daemon -S -x $DAEMON -- $ARGUMENTS
	echo "."
	;;

  status)
	status $DAEMON
	exit $?
  ;;

  *)
	echo "Usage: /etc/init.d/tc-usb-manager {start|stop|status|restart}"
	exit 1
esac

exit 0
