# Check the Telechips license
LICENSE = "Telechips"
SECTION = "hsm"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-telechips-bsp/licenses/Telechips;md5=e23a23ed6facb2366525db53060c05a4"


# Download hsm_test code through git url
SRC_URI = "${TELECHIPS_AUTOMOTIVE_GIT}/hsm.git;protocol=${ALS_GIT_PROTOCOL};branch=${ALS_BRANCH}"

SRCREV = "386689cf6e2647e24100483a03cd5a8ddef66edd"
do_compile[noexec] = "1"

INSANE_SKIP_${PN}-dev = "ldflags already-stripped dev-elf dev-deps"
INSANE_SKIP_${PN} = "ldflags dev-so dev-deps already-stripped"

S = "${WORKDIR}/git"
B = "${WORKDIR}"

HSM_BIT_arm = "32"
HSM_BIT_aarch64 = "64"

do_install() {
	install -d ${D}${bindir}
	if ${@bb.utils.contains_any('TCC_ARCH_FAMILY', "tcc803x tcc805x", 'true', 'false', d)}; then
		make -C ${S}/hsm_test/${TCC_ARCH_FAMILY} TARGET_BIT=${HSM_BIT} CHIP=${TCC_ARCH_FAMILY} YOCTO_BUILD=y
		install -m 0755 ${S}/hsm_test/${TCC_ARCH_FAMILY}/out/hsm_test_${HSM_BIT}_${TCC_ARCH_FAMILY} ${D}${bindir}/
	fi
}
