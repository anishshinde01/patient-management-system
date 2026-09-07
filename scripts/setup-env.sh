#!/bin/bash
set -e

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

SECRET=$(openssl rand -base64 32)

cat > "$PROJECT_ROOT/.env" << EOF
JWT_SECRET=$SECRET
EOF

echo ".env created with a generated JWT secret."