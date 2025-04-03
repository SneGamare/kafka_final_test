# Test health endpoint
Write-Host "Testing health endpoint..."
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/kafka/health" -Method Get
Write-Host "Health check response: $response"

# Test sending a sample message
Write-Host "`nTesting sample message endpoint..."
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/kafka/send" -Method Post
Write-Host "Sample message response: $response"

# Test sending a custom message
Write-Host "`nTesting custom message endpoint..."
$body = @{
    foracid = "9876543210"
    acctname = "Custom Account"
    lastTranDateCr = "2024-03-21"
    tranDate = "2024-03-21"
    tranId = "T98765"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/kafka/send/custom" -Method Post -Body $body -ContentType "application/json"
Write-Host "Custom message response: $response"

# Test retrieving messages
Write-Host "`nTesting message retrieval endpoint..."
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/kafka/messages" -Method Get
Write-Host "Retrieved messages:"
$response | ConvertTo-Json -Depth 10 