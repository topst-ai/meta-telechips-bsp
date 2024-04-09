MODVARS = "prepare scripts_basic scripts"

do_configure() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	for t in ${MODVARS}; do
		oe_runmake CC="${KERNEL_CC}" LD="${KERNEL_LD}" AR="${KERNEL_AR}" \
		   -C ${STAGING_KERNEL_DIR} O=${STAGING_KERNEL_BUILDDIR} $t
	done
}
