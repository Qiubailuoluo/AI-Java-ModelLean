param(
    [string]$BaseUrl = 'http://127.0.0.1:18080',
    [string]$ReportPath
)

# 在 PowerShell 7 中运行。只通过 HTTP 验收；成功创建一条练习数据并保留。
$ErrorActionPreference = 'Stop'
$checks = [System.Collections.Generic.List[object]]::new()

function Invoke-Check {
    param($Method, $Path, $Status, $Code, $JsonBody = $null)
    $options = @{
        Uri = $BaseUrl.TrimEnd('/') + $Path
        Method = $Method
        SkipHttpErrorCheck = $true
        TimeoutSec = 15
    }
    if ($null -ne $JsonBody) {
        $options.ContentType = 'application/json; charset=utf-8'
        $options.Body = [System.Text.Encoding]::UTF8.GetBytes($JsonBody)
    }
    $response = Invoke-WebRequest @options
    $result = $response.Content | ConvertFrom-Json
    if ([int]$response.StatusCode -ne $Status -or $result.code -ne $Code -or
        -not ($result.PSObject.Properties.Name -contains 'data') -or
        -not ($result.PSObject.Properties.Name -contains 'message')) {
        throw "失败：$Method $Path，实际 $($response.StatusCode)：$($response.Content)"
    }
    if ($Status -ge 400 -and ($null -ne $result.data -or
        $result.PSObject.Properties.Name -contains 'trace' -or
        $result.PSObject.Properties.Name -contains 'cause')) {
        throw "异常响应结构不符合约定：$Method $Path"
    }
    $checks.Add(@{method=$Method;path=$Path;status=$Status;code=$Code})
    Write-Host "PASS $Method $Path -> $Status $Code"
    return $result
}

# 串起新增与查询，不依赖其他机器上存在相同自增ID。
$username = 'accept_' + [guid]::NewGuid().ToString('N').Substring(0, 12)
$payload = @{username=$username;nickname='模块验收用户'} | ConvertTo-Json
$created = Invoke-Check POST '/api/users' 201 'SUCCESS' $payload
if ($created.data.id -le 0 -or $created.data.username -ne $username -or
    $created.data.nickname -ne '模块验收用户' -or -not $created.data.createdAt) {
    throw '创建响应的业务数据不正确。'
}
$userId = $created.data.id
$queried = Invoke-Check GET "/api/users/$userId" 200 'SUCCESS'
if ($queried.data.id -ne $userId -or $queried.data.username -ne $username -or
    $queried.data.nickname -ne $created.data.nickname -or
    $queried.data.createdAt -ne $created.data.createdAt) {
    throw '查询结果与创建结果不一致。'
}

$duplicate = @{username=$username.ToUpperInvariant();nickname='同名验证'} | ConvertTo-Json
$null = Invoke-Check POST '/api/users' 409 'USERNAME_ALREADY_EXISTS' $duplicate
$null = Invoke-Check POST '/api/users' 400 'INVALID_ARGUMENT' '{"username":"a b","nickname":"   "}'
$null = Invoke-Check POST '/api/users' 400 'INVALID_ARGUMENT' '{broken'
$null = Invoke-Check POST '/api/users' 400 'INVALID_ARGUMENT' ''
$null = Invoke-Check GET '/api/users/9223372036854775807' 404 'USER_NOT_FOUND'
$null = Invoke-Check GET '/api/users/0' 400 'INVALID_ARGUMENT'
$null = Invoke-Check GET '/api/users/-1' 400 'INVALID_ARGUMENT'
$null = Invoke-Check GET '/api/users/abc' 400 'INVALID_ARGUMENT'
$null = Invoke-Check GET '/api/users/9223372036854775808' 400 'INVALID_ARGUMENT'
$null = Invoke-Check GET '/no-such-lab-route' 404 'HTTP_ERROR'
$null = Invoke-Check PUT '/api/users' 405 'HTTP_ERROR'

$report = [ordered]@{
    module='01-web-foundation'
    passed=$true
    cases=$checks.Count
    preservedUser=@{id=$userId;username=$username;nickname=$created.data.nickname;createdAt=$created.data.createdAt}
    checks=@($checks.ToArray())
}
if ($ReportPath) {
    $report | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $ReportPath -Encoding utf8NoBOM
}
Write-Host "ALL_PASSED=$($checks.Count) PRESERVED_USER_ID=$userId USERNAME=$username"
