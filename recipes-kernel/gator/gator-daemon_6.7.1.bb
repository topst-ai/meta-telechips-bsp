DESCRIPTION = "ARM Gator Service Daemon"
SECTION = "applications"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "file://gator_${PV}.tar.gz \
	file://0001-modify-makefile-for-Yocto-Project.patch;striplevel=2 \
	file://gatord.service \
"
SRC_URI[md5sum] = "7892398d0088c32c928def1127bafb99"
SRC_URI[sha256sum] = "08c0e4138443b0ab4eea70551892915dda8c2cbef9ea2a78c445222ec53e9bf4"

REQUIRED_DISTRO_FEATURES += "systemd opengl wayland"
DEPENDS += "binutils-cross-${TARGET_ARCH}"

inherit systemd

S = "${WORKDIR}/gator/daemon"

# for systemd
SYSTEMD_PACKAGES = "${PN}"

MakefileName_aarch64 = "Makefile_aarch64"
MakefileName_arm = "Makefile"

do_compile() {
	oe_runmake -f ${MakefileName} V=1 || die "make failed"
}

do_install() {
	install -d ${D}${bindir}
	cp -f ${S}/gatord ${D}${bindir}

	install -d ${D}/${systemd_unitdir}/system
	install -m 644 ${WORKDIR}/gatord.service	${D}/${systemd_unitdir}/system/gatord.service
}

FILES_${PN} += "${systemd_unitdir}"
INSANE_SKIP_${PN} += "already-stripped"
