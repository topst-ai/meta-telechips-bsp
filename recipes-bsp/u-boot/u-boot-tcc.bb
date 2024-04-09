SUMMARY = "Universal Boot Loader for embedded devices"
HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"

SRC_URI = "${TELECHIPS_AUTOMOTIVE_BSP_GIT}/u-boot.git;protocol=${ALS_GIT_PROTOCOL};branch=${ALS_BRANCH}"

SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'hud-display', 'file://add-dp.cfg', '', d)}"
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'early-camera', 'file://add-earlycam.cfg', '', d)}"
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'gpu-vz', 'file://gpu-vz.cfg', '', d)}"

# DP to HDMI (1920x1080)
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'dp2hdmi', 'file://add-dp2hdmi.cfg', '', d)}"

# DP MST
# if INVITE_PLATFORM is dp-mst and MACHINE is tcc8050-main, add add-dpmst.cfg
CFG_DP_MST = "${@bb.utils.contains_any('MACHINE', 'tcc8050-main', 'file://add-dpmst.cfg', '', d)}"
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'dp-mst', '${CFG_DP_MST}', '', d)}"

SRC_URI_append_tcc803x = " ${@bb.utils.contains('INVITE_PLATFORM', 'with-subcore', 'file://add-subcore.cfg', '', d)}"
SRC_URI_append_tcc803x = " ${@bb.utils.contains('INVITE_PLATFORM', 'cluster-display', 'file://add-dlvds.cfg', '', d)}"
SRC_URI_append = "file://dtc-lexer.patch"

SRCREV = "7a0a98c19439c5bc5ee59197a738f7dd6651aabf"
#SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"
B = "${S}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit uboot-config deploy

DEPENDS += "bison-native"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" V=1'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE += 'STAGING_INCDIR=${STAGING_INCDIR_NATIVE} STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE}'

UBOOT_ARCH_ARGS_arm = "ARCH=arm"
UBOOT_ARCH_ARGS_aarch64 = "ARCH=arm64 "

SNOR_BOOT_NAME = "${TCC_ARCH_FAMILY}_snor_boot.rom"

PATCHTOOL = "git"

def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.cfg'):
            sources_list.append(s)

    return sources_list

do_configure_append() {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ] ; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' ${S}/config.mk
	fi

	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS
	if [ -n "${EXTERNALSRC}" ] ; then
		export KBUILD_OUTPUT=${EXTERNALSRC}/${MACHINE}/
	fi
	export ${UBOOT_ARCH_ARGS} DEVICE_TREE=${UBOOT_DEVICE_TREE}
	oe_runmake ${UBOOT_MACHINE}
	if [ -n "${EXTERNALSRC}" ] ; then
		${S}/scripts/kconfig/merge_config.sh -O ${KBUILD_OUTPUT} -m ${KBUILD_OUTPUT}.config ${@" ".join(find_cfgs(d))}
	else
		${S}/scripts/kconfig/merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
	fi
}

do_compile() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS
	if [ -n "${EXTERNALSRC}" ] ; then
		export KBUILD_OUTPUT=${EXTERNALSRC}/${MACHINE}/
	fi
	export ${UBOOT_ARCH_ARGS} DEVICE_TREE=${UBOOT_DEVICE_TREE}

	oe_runmake
}

do_install() {
	install -d ${D}/boot

	if [ -n "${EXTERNALSRC}" ] ; then
		export KBUILD_OUTPUT=${EXTERNALSRC}/${MACHINE}/
		install -m 0644 ${KBUILD_OUTPUT}${UBOOT_NAME}.${UBOOT_SUFFIX}	${D}/boot/${UBOOT_IMAGE}
	else
		install -m 0644 ${S}/${UBOOT_NAME}.${UBOOT_SUFFIX}	${D}/boot/${UBOOT_IMAGE}
	fi

	if ${@bb.utils.contains('INVITE_PLATFORM', 'early-camera', 'true', 'false', d)}; then
		install -m 0644 ${S}/drivers/camera/splash/${SPLASH_IMAGE}  ${D}/boot/${SPLASH_IMAGE}
	fi
}

FILES_${PN} = "/boot"

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${D}/boot/${UBOOT_IMAGE} ${DEPLOYDIR}

	cd ${DEPLOYDIR}
    rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
	cd -

	if ${@bb.utils.contains('INVITE_PLATFORM', 'early-camera', 'true', 'false', d)}; then
        install -m 0644 ${D}/boot/${SPLASH_IMAGE} ${DEPLOYDIR}
    fi
}

do_clean_extworkdir() {
	if [ -n "${EXTERNALSRC}" ]; then
		rm -rf ${EXTERNALSRC}/arch/*/include/asm/telechips
		rm -rf ${EXTERNALSRC}/${MACHINE}
	fi
}

addtask deploy before do_build after do_install
addtask clean_extworkdir after do_buildclean before do_clean
