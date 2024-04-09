DESCRIPTION = "Telechips Make Image tools"
LICENSE = "Telechips"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-telechips/meta-core/licenses/Telechips;md5=e23a23ed6facb2366525db53060c05a4"
SECTION = "bsp"

inherit native

SRC_URI = "${TELECHIPS_AUTOMOTIVE_GIT}/mktcimg.git;protocol=${ALS_GIT_PROTOCOL};branch=${ALS_BRANCH}"
SRCREV = "8bc51a87b06cfc696a17d7639ece7e0819b25a41"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/mktcimg		${D}${bindir}
}

do_configure[noexec] = "1"
do_compile[noexec] = "1"

BBCLASSEXTEND = "native"
