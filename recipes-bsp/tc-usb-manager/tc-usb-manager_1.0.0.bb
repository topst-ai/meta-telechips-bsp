DESCRIPTION = "Telechips usb manager daemon"
SECTION = "apps"
LICENSE = "Telechips"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-telechips-bsp/licenses/Telechips;md5=e23a23ed6facb2366525db53060c05a4"
DEPENDS += "dbus liblog"

SRC_URI = "file://umd;name=um \
		   ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'file://tc-usb-manager.init.sh', 'file://tc-usb-manager.service', d)} \
"

SRC_URI[um.md5sum] = "a3e9570dd8153a7d6b4690bb48993138"
SRC_URI[um.sha256sum] = "642d76de85a1dc60120cd0656b883329697727a5ad8556ee8d33006f143d73fd"
S = "${WORKDIR}"

UPDATE_RCD := "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', 'update-rc.d', d)}"

inherit autotools ${UPDATE_RCD}

# for systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "tc-usb-manager.service \
"

# for sysvinit
INIT_NAME = "tc-usb-manager"
INITSCRIPT_NAME = "${INIT_NAME}"
INITSCRIPT_PARAMS = "start 93 2 5 . stop 20 0 1 6 ."
do_preconfigure[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/umd ${D}${bindir}

	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/tc-usb-manager.service ${D}${systemd_unitdir}/system
	else
		install -d ${D}${sysconfdir}/init.d
		install -m 0755 ${WORKDIR}/tc-usb-manager.init.sh ${D}${sysconfdir}/init.d/${INIT_NAME}
	fi
}

FILES_${PN} += " \
		${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}', '', d)} \
		"

RDEPENDS_${PN} += "libusb1 libtcutils"

