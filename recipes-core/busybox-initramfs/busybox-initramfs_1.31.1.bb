FILESEXTRAPATHS_append := ":${COREBASE}/meta/recipes-core/busybox/busybox"
FILESEXTRAPATHS_append := ":${COREBASE}/meta/recipes-core/busybox/busybox-1.31.0"
FILESEXTRAPATHS_append := ":${COREBASE}/meta/recipes-core/busybox/files"

require recipes-core/busybox/busybox_${PV}.bb

DEPENDS += "virtual/crypt"

S = "${WORKDIR}/busybox-${PV}"

SRC_URI_remove = "file://defconfig"
SRC_URI += "\
	file://initramfs-defconfig \
    file://pivot_root.init \
"

do_prepare_config () {
	sed -e 's#@DATADIR@#${datadir}#g' \
		< ${WORKDIR}/initramfs-defconfig > ${S}/.config
	for i in 'CROSS' 'DISTRO FEATURES'; do echo "### $i"; done >> \
		${S}/.config
	sed -i -e '${configmangle}' ${S}/.config
	if test ${DO_IPv4} -eq 0 && test ${DO_IPv6} -eq 0; then
		# disable networking applets
		mv ${S}/.config ${S}/.config.oe-tmp
		awk 'BEGIN{net=0}
		/^# Networking Utilities/{net=1}
		/^#$/{if(net){net=net+1}}
		{if(net==2&&$0 !~ /^#/&&$1){print("# "$1" is not set")}else{print}}' \
		${S}/.config.oe-tmp > ${S}/.config
	fi
	sed -i 's/CONFIG_IFUPDOWN_UDHCPC_CMD_OPTIONS="-R -n"/CONFIG_IFUPDOWN_UDHCPC_CMD_OPTIONS="-R -b"/' ${S}/.config
}

do_install_append() {
	install -m 0755 ${WORKDIR}/pivot_root.init ${D}/init
	sed -e "s%\(^MOUNT_OPTIONS=\"\)%\1${SWITCH_ROOTFS_MOUNT_OPT}%g" -i ${D}/init
	rm -rf ${D}/lib
}

FILES_${PN} += " \
	/init \
"


PACKAGES = "${PN} ${PN}-dev ${PN}-dbg"

INITSCRIPT_PACKAGES = ""

SYSTEMD_PACKAGES = ""
SYSTEMD_SERVICE_${PN}-syslog = ""

RRECOMMENDS_${PN} = ""
