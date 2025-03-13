#!/bin/sh

# Wait for Vault to be fully available
until vault status > /dev/null 2>&1
do
  echo "Waiting for Vault to start..."
  sleep 1
done

# Enable the KV secrets engine
vault secrets enable -version=2 kv

# Create a policy for the application
cat > /tmp/app-policy.hcl << EOF
path "kv/data/chatop/*" {
  capabilities = ["read"]
}
EOF

vault policy write chatop-policy /tmp/app-policy.hcl

# Add secrets for your application
vault kv put kv/chatop/database \
  spring.datasource.url="jdbc:mysql://db:3306/chatop?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  spring.datasource.username="chatopuser" \
  spring.datasource.password="chatoppass"

vault kv put kv/chatop/jwt \
  jwt.secret="MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=" \
  jwt.expiration="86400000"

echo "Vault initialization completed successfully!"
 
#  The script above waits for Vault to be fully available, enables the KV secrets engine, creates a policy for the application, and adds secrets for the application. 
#  The script is executed when the Vault container starts. 
