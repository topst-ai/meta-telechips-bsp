#@TYPE: Machine
#@NAME: Telechips SOC Family
#@DESCRIPTION: Machine configuration for the Telechips EVM Board

# Ship all kernel modules by default
MACHINE_EXTRA_RRECOMMENDS = "kernel-modules"

DEFAULT_IMAGE_FSTYPE ?= "ext4"
SWITCH_ROOTFS_MOUNT_OPT ?= "-o ro,noatime,noload"
MKUBIFS_ARGS = "--min-io-size 4096 --leb-size 516096 --max-leb-cnt 8139"

TCC_ARCH_FAMILY ?= "${@d.getVar("SOC_FAMILY").split(":")[0]}"
TCC_MACHINE_FAMILY ?= "${TCC_ARCH_FAMILY}-${@d.getVar("SOC_FAMILY").split("-")[1]}"

PREFERRED_PROVIDER_virtual/kernel = "linux-telechips"

MACHINE_FEATURES = "usbgadget usbhost vfat alsa"
MACHINE_FEATURES += "${@oe.utils.conditional('BOOT_STORAGE', 'ufs', 'ufs', 'emmc', d)}"

# This variable is used turn on usb power(vbus) in tc-enable-removable-disk_1.0.0.bb file
# You must set this variable for turn on usb power when usb media player ready
# It is name of pid(/var/run/MediaPlayer.pid) that is usb media player process
# For more information of usb power, refer to "TCCxxxx Auto.CE Common BSP-User Guide for Drivers" document
MEDIA_PLAYER_NAME ?= "TMPlayerGUI"

# initramfs -> pivot root
# default rootfs is initramfs
# If you do not want to use the initramfs,
# you have to set 'INITRAMFS_IMAGE' and 'INITRAMFS_TASK' to NULL("") on your conf/local.conf

INITRAMFS_IMAGE ?= "initramfs-telechips-image"
INITRAMFS_TASK ?= "${INITRAMFS_IMAGE}:do_image_complete"
INITRAMFS_IMAGE_BUNDLE = "1"

EXTRA_CMDLINE += "${@bb.utils.contains('INVITE_PLATFORM', 'bootchart', 'bootchart printk.time=1 initcall_debug', '', d)}"
EXTRA_CMDLINE += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'fsck.repair=yes', '', d)}"
CMDLINE ?= "'root=/dev/ram quiet ${EXTRA_CMDLINE}'"

KERNEL_MACH_PATH ?= "arch/arm/mach-${TCC_ARCH_FAMILY}"
TARGET_ARCH_ALIAS ?= "${TARGET_ARCH}"
TARGET_ARCH_ALIAS_aarch64 ?= "arm64"

# for fix as follow issue
# Assertion 'pthread_mutex_unlock(&m->mutex) == 0' failed at ../../pulseaudio-9.0/src/pulsecore/mutex-posix.c:108,
# function pa_mutex_unlock(). Aborting.
OLDEST_KERNEL = "4.14"

require conf/machine/include/soc-family.inc

#TARGET_CC_KERNEL_ARCH_remove = "-mno-thumb-interwork -marm"
# ddr3 type option: SK_HYNIX, WINBOND
DDR3_TYPE ??= "SK_HYNIX"
# sk hynix ddr type : H5TQ4G83AFR_PBC H5TC4G83MFR_H9A H5TQ1G83BFR_H9C H5TQ2G63BFR_H9C H5TQ2G63BFR_PBC H5TQ2G83BFR_H9C H5TQ2G83BFR_PBC
# winbond ddr type : W632GG8KB_12

DDR3_SK_HYNIX_DEFINE ??="H5TQ2G83BFR_PBC"
DDR3_WINBOND_DEFINE ??="W632GG8KB_12"

SND_CARD ??= "WM8731"

EGLFS_FB ??="fb0"
USE_SOFTAP ??= "0"
USE_VPU_ENCODER ??= "0"
MY_MAC_ADDRESS ?= "not set"
MY_IP_ADDRESS ?= "not set"
MY_GATEWAY_ADDRESS ?= "not set"
USE_KEY ??= "0"
USE_USB_TO_ETHERNET ??= "0"
USE_RNDIS_HOST ??= "0"
USE_IP_NETFILTER ??= "0"
IP_MASQUERADE_INTERFACE ??= "eth0"
IP_FORWARD_INTERFACE ??= "eth1"

INVITE_PLATFORM += "${@bb.utils.contains('EXTRA_IMAGE_FEATURES', 'ssh-server-openssh', 'network', '', d)}"

LINUX_VERSION ?= "5.4.159"
LINUXLIBCVERSION ?= "5.4"

LINUX_VERSION_EXTENSION = "-tcc"

GLIBCVERSION ?= "${@bb.utils.contains('TUNE_ARCH', 'arm', '2.32%', '2.31%', d)}"

FWDN_VERSION = "8"
DATA_PART_FSTYPE ?= "ext4"

SPLASH_IMAGE ?= "${@oe.utils.conditional('BOOT_STORAGE', 'ufs', 'splash_1920x720x32_ufs.img', 'splash_1920x720x32.img', d)}"
SPLASH_IMAGE_tcc803x = "splash_1920x720x32_tcc803xp.img"
