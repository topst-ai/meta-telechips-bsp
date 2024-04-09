require linux-telechips.inc

SRC_URI = "${TELECHIPS_AUTOMOTIVE_BSP_GIT}/kernel-5.4.git;protocol=${ALS_GIT_PROTOCOL};branch=${ALS_BRANCH}"
SRCREV = "976567a560e0c42efcb16d1984a2664eaa529626"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

LINUX_VERSION = "5.4.159"
COMPATIBLE_MACHINE = "(tcc803x|tcc805x)"
KERNEL_EXTRA_ARGS_arm_append = " ARCH=arm"
KERNEL_EXTRA_ARGS_aarch64_append = " ARCH=arm64"

KERNEL_OFFSET_arm = "0x8000"
KERNEL_OFFSET_aarch64 = "0x80000"

SRC_URI += "file://0001-pcie-update-pcie-controller-driver-to-latest.patch"
#SRC_URI += "file://0000-enable-pci-host.patch"

do_change_defconfig_append() {
	if ${@bb.utils.contains_any('INVITE_PLATFORM', 'with-subcore', 'true', 'false', d)}; then
		echo "CONFIG_CAMIPC=y"						>> ${WORKDIR}/defconfig
	fi
}

kernel_do_install_append() {
	if [ -e "${D}${KERNEL_SRC_PATH}/tools/gator/daemon/escape" ]; then
		rm ${D}${KERNEL_SRC_PATH}/tools/gator/daemon/escape
	fi

	rm -rf ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/char/vpu/vpu_hevc_enc_lib*
	rm -rf ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/char/vpu/vpu_lib*
	rm -rf ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/char/vpu/vpu_4k_d2_lib*
	rm -rf ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/char/vpu/jpu_lib*
}
