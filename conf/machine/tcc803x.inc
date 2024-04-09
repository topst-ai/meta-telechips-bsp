#@TYPE: Machine
#@NAME: Telechips TCC803x Family
#@DESCRIPTION: Machine configuration for the Telechips EVB

require conf/machine/tcc-family.inc

ARCH_INC_PATH = "${@bb.utils.contains('ARCHITECTURE', 'arm', '', '', d)}"

require ${ARCH_INC_PATH}conf/machine/include/tune-cortexa53.inc
PREFERRED_PROVIDER_virtual/kernel = "linux-telechips"

GST_ARCH_FAMILY_NAME = "TCC_803x"
LCD_TYPE ??= "TIANMA"
LCD_WIDTH ??= "1920"
LCD_HEIGHT ??= "720"
TOUCH_SCREEN_TYPE ??= ""
SND_MACHINE_MODULE_NAME ??= ""

SERIAL_CONSOLES = "115200;ttyAMA0"
KERNEL_CONSOLE = "ttyAMA0"

CONFLICT_MACHINE_FEATURES = "ufs"

EXTRA_IMAGEDEPENDS += "u-boot-tcc"
EXTRA_OMX_MACHINE_CONF = "--enable-h265dec"

UBOOT_NAME ??= "u-boot"
UBOOT_MACHINE ??= "tcc803x_defconfig"
UBOOT_MAKE_TARGET ??= "tcc803x"
UBOOT_SUFFIX ?= "rom"
UBOOT_IMAGE ?= "${UBOOT_NAME}-${MACHINE}-${PV}-${PR}.${UBOOT_SUFFIX}"
UBOOT_BINARY ?= "${UBOOT_NAME}.${UBOOT_SUFFIX}"
UBOOT_SYMLINK ?= "${UBOOT_NAME}-${MACHINE}.${UBOOT_SUFFIX}"

KBUILD_DEFCONFIG ??= "tcc803x_linux_ivi_defconfig"

KERNEL_BASE_ADDR = "0x20000000"

SND_CARD ??= "AK4601"

INVITE_PLATFORM += "telechips-egl"

PREFERRED_PROVIDER_virtual/egl			= "telechips-egl"
PREFERRED_PROVIDER_virtual/libgles1		= "telechips-egl"
PREFERRED_PROVIDER_virtual/libgles2		= "telechips-egl"
PREFERRED_PROVIDER_virtual/mesa			= "telechips-egl"
PREFERRED_PROVIDER_libglapi				= "telechips-egl"
PREFERRED_PROVIDER_libgbm				= "telechips-egl"
PREFERRED_PROVIDER_libwayland-egl		= "telechips-egl"
