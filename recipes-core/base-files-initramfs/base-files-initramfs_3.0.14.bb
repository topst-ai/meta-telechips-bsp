SUMMARY = "Miscellaneous files for the base system"
DESCRIPTION = "The base-files package creates the basic system directory structure and provides a small set of key configuration files for the system."
SECTION = "base"
PR = "r89"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://licenses/GPL-2;md5=94d55d512a9ba36caa9b7df079bae19f"
# Removed all license related tasks in this recipe as license.bbclass 
# now deals with this. In order to get accurate licensing on to the image:
# Set COPY_LIC_MANIFEST to just copy just the license.manifest to the image
# For the manifest and the license text for each package:
# Set COPY_LIC_MANIFEST and COPY_LIC_DIRS

SRC_URI = "file://licenses/GPL-2"

S = "${WORKDIR}"

docdir_append = "/${P}"
dirs1777 = "/tmp"
dirs2775 = ""
dirs755 = "/dev /proc /sys ${sysconfdir}"
dirs755-lsb = ""
dirs2775-lsb = ""

volatiles = ""
conffiles = ""

do_install () {
	for d in ${dirs755}; do
		install -m 0755 -d "${D}$d"
	done
	for d in ${dirs1777}; do
		install -m 1777 -d "${D}$d"
	done
	for d in ${dirs2775}; do
		install -m 2755 -d "${D}$d"
	done
	for d in ${volatiles}; do
                if [ -d "${D}${localstatedir}/volatile/$d" ]; then
                        ln -sf volatile/$d "${D}/${localstatedir}/$d"
                fi
	done

	ln -sf /proc/mounts ${D}${sysconfdir}/mtab

	mknod -m 622 ${D}/dev/console c 5 1
}

PACKAGES = "${PN}-doc ${PN} ${PN}-dev ${PN}-dbg"
FILES_${PN} = "/"
FILES_${PN}-doc = "${docdir} ${datadir}/common-licenses"

PACKAGE_ARCH = "${MACHINE_ARCH}"
