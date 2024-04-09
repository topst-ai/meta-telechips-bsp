
do_install_append_tcc897x() {
	if ${@bb.utils.contains('INVITE_PLATFORM', 'optee', 'true', 'false', d)}; then
		install -m 0644 ${S}/lib/libhdcp1xca.so		${D}${libdir}/
		install -m 0644 ${B}/TAs/b347770c-4e41-c62b-59f4-5bbaac805e49.ta	${D}${libdir}/optee_armtz/
	fi
}
