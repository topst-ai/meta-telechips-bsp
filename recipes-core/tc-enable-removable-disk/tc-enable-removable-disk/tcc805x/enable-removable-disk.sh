#!/bin/sh
### BEGIN INIT INFO
# Provides:             Telechips Enable Removable Disk
# Required-Start:
# Required-Stop:
# Default-Start:        5
# Default-Stop:         0
# Short-Description:    Script to enable removable disk
# Description:          Script to enable removable disk after application ready.
### END INIT INFO
#
# -*- coding: utf-8 -*-
# Debian init.d script for Telechips Launcher
# Copyright © 2014 Wily Taekhyun Shin <thshin@telechips.com>

case "$1" in
  start)
	. /etc/profile
	if [ -f /sys/devices/platform/11a00000.ehci/vbus ]; then
		echo 1 > /sys/module/ehci_tcc/parameters/vbus_control_enable
		echo on > /sys/devices/platform/11a00000.ehci/vbus
	fi

	if [ -f /sys/devices/platform/11980000.dwc_otg/vbus ]; then
		echo 1 > /sys/module/dwc2/parameters/vbus_control_enable
		echo on > /sys/devices/platform/11980000.dwc_otg/vbus
	fi

	if [ -f /sys/devices/platform/dwc3_platform/vbus ]; then
		echo 1 > /sys/module/dwc3_tcc/parameters/vbus_control_enable
		echo on > /sys/devices/platform/dwc3_platform/vbus
	fi

	udevadm trigger --action=add --subsystem-match=block --sysname-match=mmcblk[1-9]*
	;;
  stop)
	. /etc/profile
	if [ -f /sys/devices/platform/11a00000.ehci/vbus ]; then
		echo off > /sys/devices/platform/11a00000.ehci/vbus
		echo 0 > /sys/module/ehci_tcc/parameters/vbus_control_enable
	fi

	if [ -f /sys/devices/platform/11980000.dwc_otg/vbus ]; then
		echo off > /sys/devices/platform/11980000.dwc_otg/vbus
		echo 0 > /sys/module/dwc2/parameters/vbus_control_enable
	fi

	if [ -f /sys/devices/platform/dwc3_platform/vbus ]; then
		echo off > /sys/devices/platform/dwc3_platform/vbus
		echo 0 > /sys/module/dwc3_tcc/parameters/vbus_control_enable
	fi

	udevadm trigger --action=remove --subsystem-match=block --sysname-match=mmcblk[1-9]*
	;;
  *)
	echo "Usage: /usr/bin/enable-removable-disk.sh {start/stop}"
	exit 1
esac

exit 0
