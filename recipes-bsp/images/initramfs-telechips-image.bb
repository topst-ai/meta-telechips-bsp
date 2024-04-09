SUMMARY = "Initramfs image for telechips kernel"
DESCRIPTION = "This image provides initramfs"
LICENSE = "MIT"

IMAGE_FSTYPES = "cpio"

# avoid circular dependencies
EXTRA_IMAGEDEPENDS = ""

IMAGE_INSTALL = "base-files-initramfs busybox-initramfs"

# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = ""

IMAGE_LINGUAS = ""
export IMAGE_BASENAME = "initramfs-telechips-image"

FEED_DEPLOYDIR_BASE_URI = ""
LDCONFIGDEPEND = ""

USE_DEVFS = "1"
USE_DEPMOD = "0"
SPLASH = ""
KERNELDEPMODDEPEND = ""
IMAGE_ROOTFS_EXTRA_SPACE = "0"

inherit core-image

#To avoid do_package WARNING, use deltask instead of noexec for do_packagedata
deltask do_packagedata
