DESCRIPTION = "Telechips prebuilt boot firmwares"
LICENSE = "Telechips"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-telechips/meta-core/licenses/Telechips;md5=e23a23ed6facb2366525db53060c05a4"
SECTION = "bsp"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/${CHIP_PATH}:"

inherit deploy

ALS_BRANCH ??= "${@bb.utils.contains('TCC_ARCH_FAMILY', 'tcc805x', 'tcc805x', 'tcc803x', d)}"

SRC_URI = "${TELECHIPS_AUTOMOTIVE_BSP_GIT}/boot-firmware_${TCC_ARCH_FAMILY}.git;protocol=${ALS_GIT_PROTOCOL};branch=${ALS_BRANCH}"
SRC_URI_append = " \
	file://fwdn.json \
	file://boot.json \
	file://micom.cfg \
"

SRCREV_tcc803x = "3599da2cb8aff0cccb1d6787fe4852aa1771a58a"
SRCREV_tcc805x = "313676601f700c5280ec045c12a25c73e9895177"

S = "${WORKDIR}/git"
PATCHTOOL = "git"

TCC8050_MACHINE_LIST = "tcc8050-main tcc8053-main tcc8050-cluster tcc8053-cluster tcc8050-dvrs tcc8053-dvrs"

CHIP_PATH = "${@d.getVar("MACHINE").split("-")[0]}"
CHIP_NAME = "${@bb.utils.contains_any('MACHINE', '${TCC8050_MACHINE_LIST}', 'tcc8050', 'tcc8059', d)}"
R5_FW_NAME = "${@bb.utils.contains_any('MACHINE', '${TCC8050_MACHINE_LIST}', 'r5_fw_TCC8050', 'r5_fw_TCC8059', d)}"

do_install_prepend_tcc805x() {
	if ${@bb.utils.contains_any('INVITE_PLATFORM', 'with-subcore', 'false', 'true', d)}; then
		sed -i 's%\(bootsel="\)dual%\1single%g'							${S}/tools/bconf_maker/tcc805x_bconf.xml
		python3 ${S}/tools/bconf_maker/bconf_maker_v01.py -i ${S}/tools/bconf_maker/tcc805x_bconf.xml -o ${S}/prebuilt/bconf.bin
	fi
}

do_install_tcc805x() {
	install -d ${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/bconf.bin 							${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/mcert.bin 							${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/dram_params.bin 						${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/hsm.bin	 							${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/hsm.cs.bin 							${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/fwdn.rom 								${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/optee.rom 							${D}/boot-firmware

	if ${@oe.utils.conditional('BOOT_STORAGE', 'ufs', 'true', 'false', d)}; then
		install -m 0644 ${S}/prebuilt/ufs/ca72_bl1.rom 		${D}/boot-firmware
		install -m 0644 ${S}/prebuilt/ufs/ca72_bl2.rom 		${D}/boot-firmware
		install -m 0644 ${S}/prebuilt/ufs/ca53_bl1.rom 		${D}/boot-firmware
		install -m 0644 ${S}/prebuilt/ufs/ca53_bl2.rom 		${D}/boot-firmware
	else
		install -m 0644 ${S}/prebuilt/ca72_bl1.rom 			${D}/boot-firmware
		install -m 0644 ${S}/prebuilt/ca72_bl2.rom 			${D}/boot-firmware
		install -m 0644 ${S}/prebuilt/ca53_bl1.rom 			${D}/boot-firmware
		install -m 0644 ${S}/prebuilt/ca53_bl2.rom 			${D}/boot-firmware
	fi

	install -m 0644 ${S}/prebuilt/scfw.rom                   ${D}/boot-firmware
	install -m 0644 ${WORKDIR}/fwdn.json					 ${D}/boot-firmware
	install -m 0644 ${WORKDIR}/boot.json					 ${D}/boot-firmware
	if ${@oe.utils.conditional('BOOT_STORAGE', 'ufs', 'true', 'false', d)}; then
		sed -i 's%emmc%ufs%g' ${D}/boot-firmware/boot.json
	fi

	sed -i 's%SOURCE_DIR%${S}%g'						${WORKDIR}/micom.cfg
	tools/snor_mkimage/tcc805x-snor-mkimage -i ${WORKDIR}/micom.cfg -o ${D}/boot-firmware/${CHIP_NAME}_snor.cs.rom
}

do_install_tcc803x() {
	install -d ${D}/boot-firmware

	install -m 0644 ${S}/prebuilt/ap.rom					 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/bconf.bin					 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/bl2-2.rom					 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/dram_params_803x.bin		 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/dram_params.bin			 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/fwdn.rom					 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/hsm.bin					 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/keypackages.bin			 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/micom-bl1.rom				 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/optee.rom					 	${D}/boot-firmware
	install -m 0644 ${S}/prebuilt/r5_sub_fw.bin				 	${D}/boot-firmware

	install -m 0644 ${WORKDIR}/fwdn.json					 	${D}/boot-firmware
	install -m 0644 ${WORKDIR}/boot.json					 	${D}/boot-firmware

	sed -i 's%SOURCE_DIR%${S}%g' ${WORKDIR}/micom.cfg
	if ${@bb.utils.contains_any('MACHINE', 'tcc8030-main tcc8030-cluster', 'true', 'false', d)}; then
		tools/d3s_snor_mkimage/tcc803x-snor-mkimage-tools -c ${WORKDIR}/micom.cfg -o ${D}/boot-firmware/tcc803x_snor_boot.rom
	else
		tools/d3s_snor_mkimage/tcc803x-snor-mkimage-tools -c ${WORKDIR}/micom.cfg -o ${D}/boot-firmware/tcc803xpe_snor_boot.rom
	fi
}

do_deploy() {
	install -d ${DEPLOYDIR}
	cp -ap ${D}/boot-firmware ${DEPLOYDIR}
}
addtask deploy after do_install

do_configure[noexec] = "1"
do_compile[noexec] = "1"

FILES_${PN} = "boot-firmware"
