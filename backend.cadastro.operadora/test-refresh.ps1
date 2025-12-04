# Script para testar o refresh token

# Aguardar API inicializar
Start-Sleep -Seconds 10

Write-Host "=== TESTANDO REFRESH TOKEN ==="

# Primeiro fazer login para obter um refresh token
$loginData = @{
    email = "jeffersoncuco89@hotmail.com"
    senha = "123456"
} | ConvertTo-Json

Write-Host "1. Fazendo login..."
$loginResponse = try {
    Invoke-RestMethod -Uri "http://localhost:8081/api/cadastro/v1/usuarios/login" -Method POST -Body $loginData -ContentType "application/json"
} catch {
    Write-Host "Erro no login: $($_.Exception.Message)"
    exit 1
}

Write-Host "Login OK - Token obtido"
$refreshToken = $loginResponse.refreshToken

# Testar refresh no header
Write-Host "2. Testando refresh com header..."
try {
    $headers = @{
        "Authorization" = "Bearer $refreshToken"
        "Content-Type" = "application/json"
    }
    $refreshResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/cadastro/v1/usuarios/refresh" -Method POST -Headers $headers
    Write-Host "Refresh com header: SUCESSO"
} catch {
    Write-Host "Refresh com header: ERRO - $($_.Exception.Message)"
}

# Testar refresh no body
Write-Host "3. Testando refresh com body..."
try {
    $refreshData = @{
        refreshToken = $refreshToken
    } | ConvertTo-Json

    $refreshResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/cadastro/v1/usuarios/refresh" -Method POST -Body $refreshData -ContentType "application/json"
    Write-Host "Refresh com body: SUCESSO"
} catch {
    Write-Host "Refresh com body: ERRO - $($_.Exception.Message)"
}

Write-Host "=== TESTE CONCLUÍDO ==="
