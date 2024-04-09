#@TYPE: Machine
#@NAME: Telechips TCC805x Family
#@DESCRIPTION: Machine configuration for the Telechips EVM Board

require conf/machine/tcc-family.inc
require conf/machine/include/tune-cortexa72.inc
PREFERRED_PROVIDER_virtual/kernel = "linux-telechips"

GST_ARCH_FAMILY_NAME = "TCC_805x"
LCD_TYPE ??= "TIANMA"
LCD_WIDTH ??= "1920"
LCD_HEIGHT ??= "720"
TOUCH_SCREEN_TYPE ??= ""
SND_MACHINE_MODULE_NAME ??= ""

BOOT_STORAGE ??= "emmc"
SERIAL_CONSOLES = "115200;ttyAMA0"
KERNEL_CONSOLE = "ttyAMA0"
# set linux-kernel and linux-libc-headers version

EXTRA_IMAGEDEPENDS += "u-boot-tcc"
EXTRA_OMX_MACHINE_CONF = "--enable-h265dec"

UBOOT_MACHINE ??= "tcc805x_defconfig"
UBOOT_MAKE_TARGET = "tcc805x"

SND_CARD ??= "AK4601"

SUBCORE_BOOT_LOADER_NAME ?= "ca53_bl3.rom"
