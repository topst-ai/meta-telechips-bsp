require optee-common.inc

OPTEE_SRC_URL = "${TELECHIPS_AUTOMOTIVE_TEE_GIT}/teeos_commercial_tcc805x.git"

SRC_URI = " \
	${@bb.utils.contains('INVITE_PLATFORM', 'optee', \
		'${OPTEE_SRC_URL};protocol=${ALS_GIT_PROTOCOL};branch=v3.14', \
		'', d)} \
"

SRCREV = "${AUTOREV}"
