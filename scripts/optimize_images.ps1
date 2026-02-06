# Optimize images using .NET System.Drawing
Add-Type -AssemblyName System.Drawing

$resPath = "app/src/main/res/drawable"
$quality = 70
$maxWidth = 600

Write-Host "🔧 Optimizing images..." -ForegroundColor Cyan
Write-Host ""

$totalBefore = 0
$totalAfter = 0

Get-ChildItem "$resPath/girl_*.jpg" | ForEach-Object {
    $imagePath = $_.FullName
    $originalSize = $_.Length
    $totalBefore += $originalSize
    
    try {
        # Load image
        $img = [System.Drawing.Image]::FromFile($imagePath)
        
        # Calculate new size
        $newWidth = $maxWidth
        $newHeight = [int]($img.Height * ($maxWidth / $img.Width))
        
        if ($img.Width -le $maxWidth) {
            $newWidth = $img.Width
            $newHeight = $img.Height
        }
        
        # Create new bitmap
        $newImg = New-Object System.Drawing.Bitmap($newWidth, $newHeight)
        $graphics = [System.Drawing.Graphics]::FromImage($newImg)
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.DrawImage($img, 0, 0, $newWidth, $newHeight)
        
        # Save with compression
        $encoderParams = New-Object System.Drawing.Imaging.EncoderParameters(1)
        $encoderParams.Param[0] = New-Object System.Drawing.Imaging.EncoderParameter(
            [System.Drawing.Imaging.Encoder]::Quality, $quality
        )
        
        $jpegCodec = [System.Drawing.Imaging.ImageCodecInfo]::GetImageEncoders() | 
            Where-Object { $_.MimeType -eq 'image/jpeg' }
        
        # Dispose original
        $img.Dispose()
        
        # Save optimized
        $newImg.Save($imagePath, $jpegCodec, $encoderParams)
        $newImg.Dispose()
        $graphics.Dispose()
        
        # Get new size
        $newSize = (Get-Item $imagePath).Length
        $totalAfter += $newSize
        $reduction = [math]::Round((($originalSize - $newSize) / $originalSize) * 100, 1)
        
        Write-Host "✅ $($_.Name): $([math]::Round($originalSize/1KB, 0))KB → $([math]::Round($newSize/1KB, 0))KB ($reduction% reduction)" -ForegroundColor Green
        
    } catch {
        Write-Host "❌ Error optimizing $($_.Name): $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "📊 Total: $([math]::Round($totalBefore/1KB, 0))KB → $([math]::Round($totalAfter/1KB, 0))KB" -ForegroundColor Yellow
Write-Host "💾 Saved: $([math]::Round(($totalBefore - $totalAfter)/1KB, 0))KB ($([math]::Round((($totalBefore - $totalAfter) / $totalBefore * 100), 1))%)" -ForegroundColor Green
