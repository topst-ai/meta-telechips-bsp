DEFAULTTUNE ?= "cortexa53"

TUNEVALID[cortexa53] = "Enable Cortex-A53 specific processor optimizations"
require conf/machine/include/arm/arch-armv8a.inc

TUNE_CCARGS .= " -mcpu=cortex-a53"
TUNE_CCARGS .= " -mtune=cortex-a53"

# AArch32 Little Endian base configs
AVAILTUNES += "cortexa53 cortexa53-crypto cortexa53-crypto"
ARMPKGARCH_tune-cortexa53        ?= "cortexa53"
ARMPKGARCH_tune-cortexa53-crypto ?= "cortexa53"
TUNE_FEATURES_tune-cortexa53         = "${TUNE_FEATURES_tune-aarch32} cortexa53"
TUNE_FEATURES_tune-cortexa53-crypto  = "${TUNE_FEATURES_tune-aarch32-crypto} cortexa53"
PACKAGE_EXTRA_ARCHS_tune-cortexa53         = "${PACKAGE_EXTRA_ARCHS_tune-aarch32} cortexa53hf-neon-fp-armv8"
PACKAGE_EXTRA_ARCHS_tune-cortexa53-crypto  = "${PACKAGE_EXTRA_ARCHS_tune-cortexa53} cortexa53hf-crypto-neon-fp-armv8"
BASE_LIB_tune-cortexa53               = "lib"
BASE_LIB_tune-cortexa53-crypto        = "lib"
