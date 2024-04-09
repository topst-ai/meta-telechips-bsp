# optee append common includes

SRC_URI_append = " file://optee.service"

INSANE_SKIP_${PN}-dev = "ldflags already-stripped dev-elf dev-deps"
INSANE_SKIP_${PN} = "ldflags dev-so dev-deps already-stripped"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "optee.service"

TEE_TARGET_POSTFIX = "${@bb.utils.contains('TARGET_FPU', 'hard', 'hf', '', d)}"
TEE_TARGET = "${GCCVERSION}/${@bb.utils.contains('TUNE_FEATURES', 'aarch64', \
		'aarch64', 'arm${TEE_TARGET_POSTFIX}', d)}"

TEEFS_IMAGE_NAME = "sest.ext4"

S = "${WORKDIR}/git/${TEE_TARGET}"
B = "${WORKDIR}/git"

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${libdir}
	install -d ${D}${libdir}/optee_armtz
	install -d ${D}${systemd_unitdir}/system
	install -d ${D}/boot
	if ${@bb.utils.contains('INVITE_PLATFORM', 'optee', 'true', 'false', d)}; then
		install -m 0755 ${S}/bin/tee-supplicant		${D}${bindir}/
		install -m 0755 ${S}/bin/tee-log		${D}${bindir}/
		install -m 0644 ${S}/lib/libteec.so		${D}${libdir}/
		install -m 0644 ${S}/lib/libca_interface.so	${D}${libdir}/
		install -m 0644 ${WORKDIR}/optee.service	${D}${systemd_unitdir}/system/optee.service
		install -m 0644 ${B}/file-system/${TEEFS_IMAGE_NAME}	${D}/boot/${TEEFS_IMAGE_NAME}
	fi
}

FILES_${PN} += " \
	${libdir}/* \
	${sbindir}/* \
	${systemd_unitdir}/* \
	/boot \
	"

do_deploy() {
	if ${@bb.utils.contains('INVITE_PLATFORM', 'optee', 'true', 'false', d)}; then
		install -d ${DEPLOYDIR}
		install -m 0644 ${D}/boot/${TEEFS_IMAGE_NAME} ${DEPLOYDIR}
	fi
}
