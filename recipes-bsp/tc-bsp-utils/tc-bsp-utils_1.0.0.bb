DESCRIPTION = "Telechips BSP Utils"
LICENSE = "Telechips"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-telechips-bsp/licenses/Telechips;md5=e23a23ed6facb2366525db53060c05a4"
SECTION = "bsp"

SRC_URI = "${TELECHIPS_AUTOMOTIVE_BSP_GIT}/util.git;protocol=${ALS_GIT_PROTOCOL};branch=${ALS_BRANCH} \
           ${@bb.utils.contains('INVITE_PLATFORM', 'dispman', \
			bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'file://${TCC_ARCH_FAMILY}/tc-dispman.init.sh', 'file://${TCC_ARCH_FAMILY}/tc-dispman.service', d), '', d)} \
"
#SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', 'file://${TCC_ARCH_FAMILY}/aarch64/libhdcp.tgz', '', d)}"
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', 'file://${TCC_ARCH_FAMILY}/aarch32/libhdcp.tgz', '', d)}"
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', 'file://firmware.le', '', d)}"
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', 'file://hdcp2.srm', '', d)}"
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', 'file://hdcp.srm', '', d)}"
SRC_URI += "${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', 'file://hdcp.conf', '', d)}"
SRCREV = "69fed3eaaf6ce879d01a0bb04729ff4bd720f35b"

S = "${WORKDIR}/git"

UPDATE_RCD := "${@bb.utils.contains('INVITE_PLATFORM', 'dispman', bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', 'update-rc.d', d), '', d)}"
inherit autotools useradd ${UPDATE_RCD}

PACKAGES =+ " \
		  tc-make-image \
		  tc-splash-tool \
		  tcsb-make-image \
		  tc-dispman \
		  tc-dispman-dev \
"

PROVIDES =+ " \
		  tc-make-image \
		  tc-splash-tool \
		  tcsb-make-image \
		  tc-dispman \
"
EXTRA_OECONF_class-native = "--enable-tc-make-image"
EXTRA_OECONF_class-native += "${@bb.utils.contains('INVITE_PLATFORM', 'secure', '--enable-tc-secure-boot', '', d)}"
EXTRA_OECONF_class-native += "${@bb.utils.contains('INVITE_PLATFORM', 'snapshot', '--enable-tc-splash-tool', '', d)}"

FILES_tc-make-image = "${bindir}/tc-lz4-demo ${bindir}/tc-make-bootimg ${bindir}/tc-make-mtdimg ${bindir}/tc-qpress ${bindir}/tc-crc ${datadir}/tc-crc/haed.bin"
FILES_tc-splash-tool = "${bindir}/tc-bmp-bpp ${bindir}/tc-make-splash ${bindir}/tc-rgb-to-565 ${bindir}/tc-rgb-to-888"
FILES_tcsb-make-image = "${bindir}/tcc-tcsb-mkimg ${bindir}/tcc-tcsb-signtool ${bindir}/tcc-tcsb-tool-v2"

#----------------------------------------------------------------------------------
# TC DISPLAY MANAGER Option
# =========================
# --enable-tc-dispman
# --enable-tc-dispman-no-daemon	: Not used
#
# Set ouput display
# =================
# --enable-tc-dispman-hdmi
# --enable-tc-dispman-component
#
# Set machine & board type
# ========================
# MACH_TYPE: if tcc893x, tcc896x, tcc897x then HDMI_V1.4
# BOARD_TYPE: if lcnv2 then -DNO_HPD_CEC_EDID
#

EXTRA_OECONF_class-target += "${@bb.utils.contains('INVITE_PLATFORM', 'dispman',\
		'--enable-tc-dispman --enable-tc-dispman-hdmi KERNEL_DIR=${STAGING_KERNEL_DIR} MACH_TYPE=${TCC_ARCH_FAMILY}', '', d)}"
EXTRA_OECONF_class-target += "${@bb.utils.contains('INVITE_PLATFORM', 'dispman',\
		oe.utils.conditional('TCC_ARCH_FAMILY', 'tcc897x', 'BOARD_TYPE=lcnv2', '', d), '', d)}"

EXTRA_OECONF_class-target += "${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi',\
		'--enable-tc-dispman-hdmi-hdcp', '', d)}"

SYSTEMD_PACKAGES = "tc-dispman"
SYSTEMD_SERVICE_tc-dispman = "tc-dispman.service"
INIT_NAME = "tc-dispman"
INITSCRIPT_NAME = "${INIT_NAME}"
INITSCRIPT_PARAMS = "start 93 5 . stop 20 0 1 6 ."
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home ${localstatedir}/lib/${INIT_NAME} \
           		     	--no-create-home --shell /bin/false \
           		    	--user-group ${PN}"
do_install_append () {
	if ${@bb.utils.contains('INVITE_PLATFORM', 'dispman', 'true', 'false', d)}; then
	    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
			install -d ${D}${sysconfdir}/init.d
			install -m 0755 ${WORKDIR}/${TCC_ARCH_FAMILY}/tc-dispman.init.sh   ${D}${sysconfdir}/init.d/${INIT_NAME}
			if ${@bb.utils.contains('INVITE_PLATFORM', 'hdmi-ext-output', 'true', 'false', d)}; then
				sed -i 's%\(^ENABLE_HDMI=\"\)%\11%g' ${D}${sysconfdir}/init.d/${INIT_NAME}
			fi
			if ${@bb.utils.contains('INVITE_PLATFORM', 'hdmi-main-output', 'true', 'false', d)}; then
				sed -i 's%\(^ENABLE_HDMI=\"\)%\11%g' ${D}${sysconfdir}/init.d/${INIT_NAME}
			fi

			install -d ${D}${sysconfdir}/default/volatiles
			echo "d ${PN} ${PN} 0755 ${localstatedir}/run/${INIT_NAME} none" \
			     > ${D}${sysconfdir}/default/volatiles/08_${INIT_NAME}
			mkdir -p ${D}${localstatedir}/lib/${INIT_NAME}
			chown ${PN}:${PN} ${D}${localstatedir}/lib/${INIT_NAME}
	    else
	        install -d ${D}${systemd_unitdir}/system
	        install -m 0644 ${WORKDIR}/${TCC_ARCH_FAMILY}/tc-dispman.service ${D}${systemd_unitdir}/system
	    fi
	fi
	if ${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', 'true', 'false', d)}; then
		install -d ${D}/usr/share/esm
		install -d ${D}/usr/tmp
		install -d ${D}/usr/etc
		install -d ${D}/usr/lib

		install -m 0644 ${WORKDIR}/hdcp.conf   ${D}/usr/etc/
		install -m 0644 ${WORKDIR}/firmware.le ${D}/usr/share/esm/
		install -m 0644 ${WORKDIR}/hdcp2.srm   ${D}/usr/tmp/
		install -m 0644 ${WORKDIR}/hdcp.srm    ${D}/usr/tmp/
		install -m 0644 ${WORKDIR}/libhdcp.so* ${D}/usr/lib/
	fi
}


ALLOW_EMPTY_${PN} = "1"

INSANE_SKIP_${PN}-dev = "already-stripped"
INSANE_SKIP_${PN} = "already-stripped"

RDEPENDS_${PN}_class-target += " \
	tc-dispman \
"
FILES_tc-dispman = " \
	${bindir}/dispman_daemon \
	${libdir}/libtcchdmi.so.1.0.0 \
	${libdir}/libtcchdmi.so.1 \
	${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/tc-dispman.service', '', d)} \
	${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', '${libdir}/libHdcpHdmi.so.1.0.0', '', d)} \
	${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', '${libdir}/libHdcpHdmi.so.1', '', d)} \
	${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', '${libdir}/libhdcp.*', '', d)} \
	${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', '/usr/share/esm/', '', d)} \
	${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', '/usr/tmp/', '', d)} \
	${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', '/usr/etc/', '', d)} \
"
FILES_tc-dispman-dev = " \
	${libdir}/libtcchdmi.so \
	${@bb.utils.contains('INVITE_PLATFORM', 'hdcp-hdmi', '${libdir}/libHdcpHdmi.so', '', d)} \
"

#----------------------------------------------------------------------------------

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
BBCLASSEXTEND = "native nativesdk"
PATCHTOOL = "git"
