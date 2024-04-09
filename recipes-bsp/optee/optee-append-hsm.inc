
SRC_URI_append_tcc803x = " \
	file://thsm.service \
"

SYSTEMD_SERVICE_${PN}_append_tcc803x = " \
	thsm.service \
"

do_install_append() {
	if ${@bb.utils.contains('INVITE_PLATFORM', 'optee', 'true', 'false', d)}; then
		if ${@bb.utils.contains_any("TCC_ARCH_FAMILY", "tcc897x tcc802x tcc803x", "true", "false", d)}; then
			install -m 0755 ${S}/bin/hsmTEEtest		${D}${bindir}/
			install -m 0644 ${S}/lib/libcaHSM.so		${D}${libdir}/
			install -m 0644 ${B}/TAs/9aec3623-b887-4491-a453-3de7735a3e85.ta	${D}${libdir}/optee_armtz/
		fi
	fi
}

do_install_append_tcc803x() {
	if ${@bb.utils.contains('INVITE_PLATFORM', 'optee', 'true', 'false', d)}; then
		install -m 0755 ${S}/bin/thsmDaemon		${D}${bindir}/
		install -m 0644 ${WORKDIR}/thsm.service		${D}${systemd_unitdir}/system/thsm.service
	fi
}
