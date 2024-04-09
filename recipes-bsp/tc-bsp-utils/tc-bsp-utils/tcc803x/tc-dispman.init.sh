#! /bin/sh
### BEGIN INIT INFO
# Provides:             Telechips Display Manager
# Required-Start:
# Required-Stop:
# Default-Start:        5
# Default-Stop:         0
# Short-Description:    Telechips Linux AVN Process
# Description:          Telechips Linux AVN Process
### END INIT INFO
#
# -*- coding: utf-8 -*-
# Debian init.d script for Telechips dispman
# Copyright © 2014 Wily Taekhyun Shin <thshin@telechips.com>

# Source function library.
. /etc/init.d/functions

DAEMON=/usr/bin/dispman_daemon
RUNDIR=/var/run/tc-dispman
DESC="telechips dispman daemon"
ARGUMENTS=""

ENABLE_HDMI=""

test -x $DAEMON || exit 0

[ -z "$SYSCONFDIR" ] && SYSCONFDIR=/var/lib/tc-dispman
mkdir -p $SYSCONFDIR

check_for_no_start() {
    # forget it if we're trying to start, and /var/lib/tc-dispman/tc-dispman_not_to_be_run exists
    if [ -e $SYSCONFDIR/tc-dispman_not_to_be_run ]; then
	echo "Telechips dispman not in use ($SYSCONFDIR/tc-dispman_not_to_be_run)"
	exit 0
    fi
}

check_privsep_dir() {
    # Create the PrivSep empty dir if necessary
    if [ ! -d /var/run/tc-dispman ]; then
		mkdir -p $RUNDIR
    fi
}

set_environment() {
        if [ ! -e /dev/i2c-ddc ]; then
                ln -s /dev/i2c-1 /dev/i2c-ddc
        fi
        if [ -e /sys/class/tcc_dispman/tcc_dispman/persist_output_mode ]; then 
      		echo 1 > /sys/class/tcc_dispman/tcc_dispman/persist_output_mode
      	fi
}

case "$1" in
  start)
	check_for_no_start

  	echo -n "Starting $DESC: "
	check_privsep_dir
	set_environment

	if [[ $TC_DISPMAN_CONF ]]; then
		ARGUMENTS="$ARGUMENTS --config=$TC_DISPMAN_CONF"
	fi

	start-stop-daemon -S -x $DAEMON -- $ARGUMENTS

#set hdmi audio output
	if [ "$ENABLE_HDMI" = "1" ]; then
		echo 1 > /sys/class/tcc_dispman/tcc_dispman/tcc_audio_hdmi_link
		echo 1 > /sys/class/tcc_dispman/tcc_dispman/persist_spdif_setting
		echo 1 > /sys/class/tcc_dispman/tcc_dispman/tcc_audio_sampling_rate
	fi

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
	sleep 2
	set_environment
	if [[ $TC_DISPMAN_CONF ]]; then
		ARGUMENTS="$ARGUMENTS --config=$TC_DISPMAN_CONF"
	fi

	start-stop-daemon -S -x $DAEMON -- $ARGUMENTS
	echo "done."
	;;

  status)
	status $DAEMON
	exit $?
  ;;

  *)
	echo "Usage: /etc/init.d/tc-dispman {start|stop|status|restart}"
	exit 1
esac

exit 0
