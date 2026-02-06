#!/usr/bin/env python3
"""
Optimize images to reduce APK size
Requires: pip install Pillow
"""

import os
from PIL import Image
import sys

def optimize_image(image_path, quality=75, max_width=800):
    """Optimize a single image"""
    try:
        with Image.open(image_path) as img:
            # Get original size
            original_size = os.path.getsize(image_path)
            
            # Convert RGBA to RGB if needed
            if img.mode in ('RGBA', 'LA', 'P'):
                background = Image.new('RGB', img.size, (255, 255, 255))
                if img.mode == 'P':
                    img = img.convert('RGBA')
                background.paste(img, mask=img.split()[-1] if img.mode == 'RGBA' else None)
                img = background
            
            # Resize if too large
            if img.width > max_width:
                ratio = max_width / img.width
                new_height = int(img.height * ratio)
                img = img.resize((max_width, new_height), Image.Resampling.LANCZOS)
            
            # Save optimized
            img.save(image_path, 'JPEG', quality=quality, optimize=True)
            
            # Get new size
            new_size = os.path.getsize(image_path)
            reduction = ((original_size - new_size) / original_size) * 100
            
            print(f"✅ {os.path.basename(image_path)}: {original_size//1024}KB → {new_size//1024}KB ({reduction:.1f}% reduction)")
            
    except Exception as e:
        print(f"❌ Error optimizing {image_path}: {e}")

def main():
    # Find all images in drawable folders
    res_path = "app/src/main/res"
    
    if not os.path.exists(res_path):
        print(f"❌ Path not found: {res_path}")
        sys.exit(1)
    
    print("🔧 Optimizing images...")
    print()
    
    total_before = 0
    total_after = 0
    
    # Optimize girl images (large photos)
    drawable_path = os.path.join(res_path, "drawable")
    if os.path.exists(drawable_path):
        for filename in os.listdir(drawable_path):
            if filename.startswith("girl_") and filename.endswith(".jpg"):
                image_path = os.path.join(drawable_path, filename)
                before = os.path.getsize(image_path)
                total_before += before
                
                optimize_image(image_path, quality=70, max_width=600)
                
                after = os.path.getsize(image_path)
                total_after += after
    
    print()
    print(f"📊 Total: {total_before//1024}KB → {total_after//1024}KB")
    print(f"💾 Saved: {(total_before - total_after)//1024}KB ({((total_before - total_after) / total_before * 100):.1f}%)")

if __name__ == "__main__":
    main()
