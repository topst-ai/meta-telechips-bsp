do_install_append() {
	rm -f $kerneldir/mkbootimg
	rm -f $kerneldir/mkimage
	rm -f $kerneldir/scripts/ver_linux
	rm -f $kerneldir/arch/x86/tools/gen-insn-attr-x86.awk
	rm -f $kerneldir/arch/x86/tools/distill.awk
	rm -f $kerneldir/arch/sh/tools/gen-mach-types
	rm -f $kerneldir/arch/arm/tools/gen-mach-types
	rm -f $kerneldir/tools/perf/util/intel-pt-decoder/gen-insn-attr-x86.awk
	rm -f $kerneldir/tools/perf/arch/x86/tests/gen-insn-x86-dat.awk
	rm -f $kerneldir/tools/objtool/arch/x86/insn/gen-insn-attr-x86.awk
}
