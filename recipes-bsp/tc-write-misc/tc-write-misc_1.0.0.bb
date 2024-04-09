DESCRIPTION = "Telechips Write Misc Tool"
LICENSE = "Telechips"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-telechips-bsp/licenses/Telechips;md5=e23a23ed6facb2366525db53060c05a4"
SECTION = "bsp"

SRC_URI = "file://tc-write-misc.c"

S = "${WORKDIR}"

do_compile () {
	$CC $LDFLAGS -o tc-write-misc $CFLAGS tc-write-misc.c
}

do_install () {
  install -d ${D}${bindir}
  install -m 0755 ${S}/tc-write-misc ${D}${bindir}/
}

