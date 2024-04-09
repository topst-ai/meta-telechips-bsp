DEFAULTTUNE ?= "cortexa72"

TUNEVALID[cortexa72] = "Enable Cortex-A72 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa72', ' -mcpu=cortex-a72', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# AArch32 Little Endian base configs
AVAILTUNES += "cortexa72 cortexa72-crypto"
ARMPKGARCH_tune-cortexa72        ?= "cortexa72"
ARMPKGARCH_tune-cortexa72-crypto ?= "cortexa72"
TUNE_FEATURES_tune-cortexa72         = "${TUNE_FEATURES_tune-aarch32} cortexa72"
TUNE_FEATURES_tune-cortexa72-crypto  = "${TUNE_FEATURES_tune-aarch32-crypto} cortexa72"
PACKAGE_EXTRA_ARCHS_tune-cortexa72         = "${PACKAGE_EXTRA_ARCHS_tune-aarch32} cortexa72hf-neon-fp-armv8"
PACKAGE_EXTRA_ARCHS_tune-cortexa72-crypto  = "${PACKAGE_EXTRA_ARCHS_tune-cortexa72} cortexa72hf-crypto-neon-fp-armv8"
BASE_LIB_tune-cortexa72               = "lib"
BASE_LIB_tune-cortexa72-crypto        = "lib"
