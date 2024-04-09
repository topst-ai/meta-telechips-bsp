# optee_%.bbappend
#

inherit systemd deploy

require optee-append.inc
require optee-append-hsm.inc
require optee-append-hdcp.inc

TEEOS_IMAGE = "optee.rom"
TEEOS_FWDN_JSON = "${TCC_ARCH_FAMILY}_optee.json"
TEEOS_UFS_JSON = "${TCC_ARCH_FAMILY}_optee_ufs.json"

do_install_append() {
	if ${@bb.utils.contains('INVITE_PLATFORM', 'optee', 'true', 'false', d)}; then
		# optee unit test
		if ${@bb.utils.contains('INVITE_PLATFORM', 'optee-xtest', 'true', 'false', d)}; then
			install -m 0755 ${B}/xtest/${TEE_TARGET}/xtest	${D}${bindir}/
			cp -rf  ${B}/xtest/TAs/*.ta			${D}${libdir}/optee_armtz/
		fi

		if ${@bb.utils.contains_any("TCC_ARCH_FAMILY", "tcc897x tcc802x", "false", "true", d)}; then
			install -d ${D}/boot
			install -m 0644 ${B}/OS/${TEEOS_IMAGE}		${D}/boot/${TEEOS_IMAGE}
			if ${@oe.utils.conditional('BOOT_STORAGE', 'ufs', 'true', 'false', d)}; then
				install -m 0644 ${B}/OS/${TEEOS_UFS_JSON}	${D}/boot/${TEEOS_FWDN_JSON}
			else
				install -m 0644 ${B}/OS/${TEEOS_FWDN_JSON}	${D}/boot/${TEEOS_FWDN_JSON}
			fi
		fi
	fi
}

FILES_${PN}_append = " \
	/boot \
"

do_deploy_append() {
	if ${@bb.utils.contains('INVITE_PLATFORM', 'optee', 'true', 'false', d)}; then
		if ${@bb.utils.contains_any("TCC_ARCH_FAMILY", "tcc897x tcc802x", "false", "true", d)}; then
			install -d ${DEPLOYDIR}
			install -m 0644 ${D}/boot/${TEEOS_IMAGE} ${DEPLOYDIR}
			install -m 0644 ${D}/boot/${TEEOS_FWDN_JSON} ${DEPLOYDIR}
		fi
	fi
}

addtask deploy before do_build after do_install
