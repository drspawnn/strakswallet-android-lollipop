# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

# now build app's shared lib
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -DANDROID_STL=gnustl_static -DANDROID_TOOLCHAIN=clang")

# -Wimplicit-function-declaration
# -Wno-missing-prototypes -Werror=return-type -Wdocumentation -Wunreachable-code-aggressive -Wno-missing-braces
# -Wparentheses -Wswitch -Wno-unused-function -Wunused-label -Wno-unused-parameter -Wunused-variable -Wunused-value -Wempty-body
# -Wconditional-uninitialized -Wno-unknown-pragmas -pedantic -Wshadow -Wfour-char-constants -Wno-conversion -Wconstant-conversion
# -Wint-conversion -Wbool-conversion -Wenum-conversion -Wassign-enum -Wno-shorten-64-to-32 -Wpointer-sign -Wnewline-eof
# -Wdeprecated-declarations -Wno-sign-conversion

add_library( # The name of the library.
             core

             # Shared library.
             SHARED

             # Core files
             src/main/jni/strakswallet-core/BRAddress.c
             src/main/jni/strakswallet-core/BRAddress.h
             src/main/jni/strakswallet-core/BRArray.h
             src/main/jni/strakswallet-core/BRBase58.c
             src/main/jni/strakswallet-core/BRBase58.h
             src/main/jni/strakswallet-core/BRBIP32Sequence.c
             src/main/jni/strakswallet-core/BRBIP32Sequence.h
             src/main/jni/strakswallet-core/BRBIP38Key.c
             src/main/jni/strakswallet-core/BRBIP38Key.h
             src/main/jni/strakswallet-core/BRBIP39Mnemonic.c
             src/main/jni/strakswallet-core/BRBIP39Mnemonic.h
             src/main/jni/strakswallet-core/BRBIP39WordsEn.h
             src/main/jni/strakswallet-core/BRBloomFilter.c
             src/main/jni/strakswallet-core/BRBloomFilter.h
             src/main/jni/strakswallet-core/BRCrypto.c
             src/main/jni/strakswallet-core/BRCrypto.h
             src/main/jni/strakswallet-core/BRInt.h
             src/main/jni/strakswallet-core/BRKey.c
             src/main/jni/strakswallet-core/BRKey.h
             src/main/jni/strakswallet-core/BRMerkleBlock.c
             src/main/jni/strakswallet-core/BRMerkleBlock.h
             src/main/jni/strakswallet-core/BRPaymentProtocol.c
             src/main/jni/strakswallet-core/BRPaymentProtocol.h
             src/main/jni/strakswallet-core/BRPeer.c
             src/main/jni/strakswallet-core/BRPeer.h
             src/main/jni/strakswallet-core/BRPeerManager.c
             src/main/jni/strakswallet-core/BRPeerManager.h
             src/main/jni/strakswallet-core/BRSet.c
             src/main/jni/strakswallet-core/BRSet.h
             src/main/jni/strakswallet-core/BRTransaction.c
             src/main/jni/strakswallet-core/BRTransaction.h
             src/main/jni/strakswallet-core/BRWallet.c
             src/main/jni/strakswallet-core/BRWallet.h

             # STRAKS hash
             src/main/jni/strakswallet-core/Lyra2RE/blake.c
             src/main/jni/strakswallet-core/Lyra2RE/bmw.c
             src/main/jni/strakswallet-core/Lyra2RE/cubehash.c
             src/main/jni/strakswallet-core/Lyra2RE/groestl.c
             src/main/jni/strakswallet-core/Lyra2RE/keccak.c
             src/main/jni/strakswallet-core/Lyra2RE/Lyra2.c
             src/main/jni/strakswallet-core/Lyra2RE/Lyra2.h
             src/main/jni/strakswallet-core/Lyra2RE/Lyra2RE.c
             src/main/jni/strakswallet-core/Lyra2RE/Lyra2RE.h
             src/main/jni/strakswallet-core/Lyra2RE/skein.c
             src/main/jni/strakswallet-core/Lyra2RE/sph_blake.h
             src/main/jni/strakswallet-core/Lyra2RE/sph_bmw.h
             src/main/jni/strakswallet-core/Lyra2RE/sph_cubehash.h
             src/main/jni/strakswallet-core/Lyra2RE/sph_groestl.h
             src/main/jni/strakswallet-core/Lyra2RE/sph_keccak.h
             src/main/jni/strakswallet-core/Lyra2RE/sph_skein.h
             src/main/jni/strakswallet-core/Lyra2RE/sph_types.h
             src/main/jni/strakswallet-core/Lyra2RE/Sponge.c
             src/main/jni/strakswallet-core/Lyra2RE/Sponge.h

             # BCash files
             src/main/jni/strakswallet-core/bcash/BRBCashAddr.c
             src/main/jni/strakswallet-core/bcash/BRBCashAddr.h
             src/main/jni/strakswallet-core/bcash/BRBCashParams.h

             # Core JNI files
             src/main/jni/strakswallet-core/Java/BRCoreJni.h
             src/main/jni/strakswallet-core/Java/BRCoreJni.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreJniReference.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreJniReference.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreAddress.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreAddress.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreChainParams.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreChainParams.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCorePeer.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCorePeer.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreMasterPubKey.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreMasterPubKey.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreKey.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreKey.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreMerkleBlock.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreMerkleBlock.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCorePeerManager.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCorePeerManager.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreTransaction.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreTransaction.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreTransactionInput.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreTransactionInput.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreTransactionOutput.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreTransactionOutput.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCorePaymentProtocol.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCorePaymentProtocol.h
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreWallet.c
             src/main/jni/strakswallet-core/Java/com_strakswallet_core_BRCoreWallet.h

             # BCash files
             src/main/jni/strakswallet-core/bcash/BRBCashAddr.c
             src/main/jni/strakswallet-core/bcash/BRBCashAddr.h
             src/main/jni/strakswallet-core/bcash/BRBCashParams.h

             # Bech32 files
             src/main/jni/strakswallet-core/BRBech32.c
             src/main/jni/strakswallet-core/BRBech32.h

             )

include_directories(src/main/jni/strakswallet-core/bcash)
include_directories(src/main/jni/strakswallet-core/Java)
include_directories(src/main/jni/strakswallet-core/)
include_directories(src/main/jni/strakswallet-core/secp256k1/include)
include_directories(src/main/jni/strakswallet-core/secp256k1/src)
include_directories(src/main/jni/strakswallet-core/secp256k1)

include_directories(src/main/secp/secp256k1/include/)
include_directories(src/main/secp/secp256k1/src/)
include_directories(src/main/secp/secp256k1/)
include_directories(src/main/secp/)

# add lib dependencies
target_link_libraries(
    core
    android
    log)
