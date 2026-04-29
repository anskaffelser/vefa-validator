#!/usr/bin/env bash
# rotate-keystore.sh
#
# Generates a new self-signed keystore for validator-build.
#
# Usage:
#   ./rotate-keystore.sh                         # ANS defaults
#   ./rotate-keystore.sh --dname "CN=My Name"    # override DN
#   ./rotate-keystore.sh --validity 1825         # shorter validity (5 years)
#   ./rotate-keystore.sh --out /tmp/test.jks     # custom destination
#   ./rotate-keystore.sh --help
#
# All flags are optional – defaults are the values used by ANS.

set -euo pipefail

# ── Defaults (ANS) ───────────────────────────────────────────────────────────
DEFAULT_ALIAS="self-signed"
DEFAULT_STOREPASS="changeit"
DEFAULT_KEYPASS="changeit"
DEFAULT_DNAME="CN=VEFA Validator self-signed, O=Anskaffelser.no, C=NO"
DEFAULT_VALIDITY=3650   # 10 years
DEFAULT_KEYSIZE=2048
DEFAULT_KEYALG="RSA"
DEFAULT_SIGALG="SHA256withRSA"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_OUT="${SCRIPT_DIR}/../src/main/resources/keystore-self-signed.jks"

# ── Argument parsing ─────────────────────────────────────────────────────────
ALIAS="$DEFAULT_ALIAS"
STOREPASS="$DEFAULT_STOREPASS"
KEYPASS="$DEFAULT_KEYPASS"
DNAME="$DEFAULT_DNAME"
VALIDITY="$DEFAULT_VALIDITY"
KEYSIZE="$DEFAULT_KEYSIZE"
KEYALG="$DEFAULT_KEYALG"
SIGALG="$DEFAULT_SIGALG"
OUT="$DEFAULT_OUT"

usage() {
  cat <<EOF
Usage: $(basename "$0") [FLAGS]

Flags:
  --alias      <value>   Key alias              (default: $DEFAULT_ALIAS)
  --storepass  <value>   Keystore password      (default: $DEFAULT_STOREPASS)
  --keypass    <value>   Key password           (default: $DEFAULT_KEYPASS)
  --dname      <value>   Distinguished Name     (default: "$DEFAULT_DNAME")
  --validity   <days>    Validity in days       (default: $DEFAULT_VALIDITY → 10 years)
  --keysize    <bits>    RSA key size           (default: $DEFAULT_KEYSIZE)
  --keyalg     <value>   Key algorithm          (default: $DEFAULT_KEYALG)
  --sigalg     <value>   Signature algorithm    (default: $DEFAULT_SIGALG)
  --out        <file>    Destination file       (default: src/main/resources/keystore-self-signed.jks)
  --help                 Show this help
EOF
  exit 0
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --alias)      ALIAS="$2";      shift 2 ;;
    --storepass)  STOREPASS="$2";  shift 2 ;;
    --keypass)    KEYPASS="$2";    shift 2 ;;
    --dname)      DNAME="$2";      shift 2 ;;
    --validity)   VALIDITY="$2";   shift 2 ;;
    --keysize)    KEYSIZE="$2";    shift 2 ;;
    --keyalg)     KEYALG="$2";     shift 2 ;;
    --sigalg)     SIGALG="$2";     shift 2 ;;
    --out)        OUT="$2";        shift 2 ;;
    --help|-h)    usage ;;
    *) echo "Unknown flag: $1" >&2; usage ;;
  esac
done

# ── Dependency check ─────────────────────────────────────────────────────────
if ! command -v keytool &> /dev/null; then
  echo "ERROR: 'keytool' not found. Make sure a Java JDK is installed and on PATH." >&2
  exit 1
fi

# ── Summary ──────────────────────────────────────────────────────────────────
OUT_ABS="$(cd "$(dirname "$OUT")" 2>/dev/null && pwd)/$(basename "$OUT")" \
  || OUT_ABS="$OUT"

echo ""
echo "=== rotate-keystore.sh ==="
echo "  Destination : $OUT_ABS"
echo "  Alias       : $ALIAS"
echo "  DN          : $DNAME"
echo "  Validity    : $VALIDITY days (~$(( VALIDITY / 365 )) years)"
echo "  Algorithm   : $KEYALG/$SIGALG, $KEYSIZE bit"
echo ""

# ── Backup existing file ─────────────────────────────────────────────────────
if [[ -f "$OUT" ]]; then
  BACKUP="${OUT}.$(date +%Y%m%d-%H%M%S).bak"
  echo "→ Backing up existing keystore: $BACKUP"
  cp "$OUT" "$BACKUP"
fi

# ── Remove old file so keytool does not prompt for overwrite ─────────────────
rm -f "$OUT"

# ── Generate new keystore ────────────────────────────────────────────────────
echo "→ Generating new keystore..."
keytool -genkeypair \
  -alias        "$ALIAS" \
  -keyalg       "$KEYALG" \
  -keysize      "$KEYSIZE" \
  -sigalg       "$SIGALG" \
  -validity     "$VALIDITY" \
  -dname        "$DNAME" \
  -keystore     "$OUT" \
  -storetype    JKS \
  -storepass    "$STOREPASS" \
  -keypass      "$KEYPASS" \
  -noprompt

# ── Verify ───────────────────────────────────────────────────────────────────
echo ""
echo "→ Verifying new keystore:"
keytool -list -v \
  -keystore "$OUT" \
  -storepass "$STOREPASS" \
  | grep -E "Alias name:|Valid from:|until:"

echo ""
echo "✓ Keystore generated: $OUT_ABS"
echo ""
echo "Next steps:"
echo "  1. Build dependencies (skip tests):  mvn -pl validator-build -am -DskipTests install --no-transfer-progress"
echo "  2. Run the validator-build test:     mvn -pl validator-build -Dtest=SimpleProjectTest#simpleWithTests test --no-transfer-progress"
echo "  3. Commit the new .jks file to the repository."
echo "  4. Build and publish a new Docker image (anskaffelser/validator:edge) if applicable."
