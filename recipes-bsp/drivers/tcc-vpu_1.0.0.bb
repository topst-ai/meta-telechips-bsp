DESCRIPTION = "Telechips VPU Drivers"

SECTION = "kernel/modules"
LICENSE = "Telechips"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-telechips-bsp/licenses/Telechips;md5=e23a23ed6facb2366525db53060c05a4"

inherit module

SRC_URI = "${TELECHIPS_AUTOMOTIVE_BSP_GIT}/vpu.git;protocol=${ALS_GIT_PROTOCOL};branch=${ALS_BRANCH}"
SRCREV = "cfc411368049ea5df507a522b8936fc32850b212"

PATCHTOOL = "git"
LINKER_HASH_STYLE = "sysv"

S="${WORKDIR}/git"

do_preconfigure[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

ALLOW_EMPTY_${PN} = "1"

PACKAGES += "kernel-modules-vpu"

TCC805x_VPU_LIB_NAME = "als_tcc805x_vpu_c7_lib_${TUNE_ARCH}_v5_4"
TCC805x_JPU_LIB_NAME = "als_tcc805x_jpu_c6_lib_${TUNE_ARCH}_v5_4"
TCC805x_VPU_4K_D2_LIB_NAME = "als_tcc805x_vpu_4k_d2_lib_${TUNE_ARCH}_v5_4"
TCC805x_VPU_HEVC_ENC_NAME = "als_tcc805x_vpu_hevc_enc_lib_${TUNE_ARCH}_v5_4"

TCC803x_VPU_LIB_NAME = "als_vpu_c7_lib_${TUNE_ARCH}_v5_4"
TCC803x_JPU_LIB_NAME = "als_jpu_c6_lib_${TUNE_ARCH}_v5_4"
TCC803x_HEVC_LIB_NAME = "als_hevc_lib_${TUNE_ARCH}_v5_4"

VPU_LIB_NAME = "${@oe.utils.conditional('TCC_ARCH_FAMILY', 'tcc805x', '${TCC805x_VPU_LIB_NAME}', '${TCC803x_VPU_LIB_NAME}', d)}"
JPU_LIB_NAME = "${@oe.utils.conditional('TCC_ARCH_FAMILY', 'tcc805x', '${TCC805x_JPU_LIB_NAME}', '${TCC803x_JPU_LIB_NAME}', d)}"
VPU_4K_D2_LIB_NAME = "${@oe.utils.conditional('TCC_ARCH_FAMILY', 'tcc805x', '${TCC805x_VPU_4K_D2_LIB_NAME}', 'not exist', d)}"
VPU_HEVC_ENC_LIB_NAME = "${@oe.utils.conditional('TCC_ARCH_FAMILY', 'tcc805x', '${TCC805x_VPU_HEVC_ENC_NAME}', '${TCC803x_HEVC_LIB_NAME}', d)}"

VPU_INSTALL_DIR = "${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/char/vpu/lib"

do_install() {
	install -d ${VPU_INSTALL_DIR}
	install -d ${D}${sysconfdir}/modules-load.d

	install -m 0644 ${S}/${VPU_LIB_NAME}.ko	${VPU_INSTALL_DIR}/vpu_lib.ko
	echo "vpu_lib"	> ${D}${sysconfdir}/modules-load.d/vpu-lib.conf

	if [ "${VPU_4K_D2_LIB_NAME}" != "not exist" ]; then
		install -m 0644 ${S}/${VPU_4K_D2_LIB_NAME}.ko	${VPU_INSTALL_DIR}/vpu_4k_d2_lib.ko
		echo "vpu_4k_d2_lib"	>> ${D}${sysconfdir}/modules-load.d/vpu-lib.conf
	fi

	if [ "${VPU_HEVC_ENC_LIB_NAME}" != "not exist" ]; then
		install -m 0644 ${S}/${VPU_HEVC_ENC_LIB_NAME}.ko	${VPU_INSTALL_DIR}/vpu_hevc_enc_lib.ko
		echo "vpu_hevc_enc_lib"	>> ${D}${sysconfdir}/modules-load.d/vpu-lib.conf
	fi

	if [ "${JPU_LIB_NAME}" != "not exist" ]; then
		install -m 0644 ${S}/${JPU_LIB_NAME}.ko	${VPU_INSTALL_DIR}/jpu_lib.ko
		echo "jpu_lib"	>> ${D}${sysconfdir}/modules-load.d/vpu-lib.conf
	fi

	# remove vpu module
	if ${@bb.utils.contains('DISTRO_FEATURES','remove-vpu-module','true','false',d)}; then
		rm -rf ${D}${sysconfdir}/modules-load.d/vpu-lib.conf
	fi
}

FILES_kernel-modules-vpu += "${sysconfdir}/modules-load.d/vpu-lib.conf"

RDEPENDS_kernel-modules-vpu += " \
	kernel-module-vpu-lib \
	${@oe.utils.conditional('VPU_4K_D2_LIB_NAME', 'not exist', '', 'kernel-module-vpu-4k-d2-lib', d)} \
	${@oe.utils.conditional('VPU_HEVC_ENC_LIB_NAME', 'not exist', '', 'kernel-module-vpu-hevc-enc-lib', d)} \
	${@oe.utils.conditional('JPU_LIB_NAME', 'not exist', '', 'kernel-module-jpu-lib', d)} \
"

RPROVIDES_${PN} += "kernel-modules-vpu"
