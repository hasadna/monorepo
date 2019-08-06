import { Injectable } from '@angular/core';

@Injectable()
export class BinaryService {
  parseImageBinary(imageBinary: string): { imageHeader: string; base64: string } {
    const imageBinaryParts: string[] = imageBinary.split(',');
    return {
      imageHeader: imageBinaryParts[0],
      base64: imageBinaryParts[1],
    };
  }

  imageHeaderToMime(imageHeader: string): string {
    // 'data:image/png;base64' -> 'image/png'
    const mime: RegExpMatchArray = imageHeader.match(/data:([\w\d]+\/[\w\d-.]+);base64/);
    if (mime && mime[1]) {
      return mime[1];
    } else {
      throw new Error('Mime not found');
    }
  }

  mimeToExtension(mime: string): string {
    // 'image/png' -> 'png'
    const mimeFormat: string = mime.split('/')[1];
    // TODO: improve this part. Some exceptions might not be handled.
    switch (mimeFormat) {
      case 'png': return 'png';
      case 'webp': return 'webp';
      case 'gif': return 'gif';
      case 'svg':
      case 'svg+xml': return 'svg';
      case 'bmp':
      case 'x-ms-bmp': return 'bmp';
      case 'ico':
      case 'icon':
      case 'x-icon': return 'ico';
      case 'jpeg':
      case 'jpg':
      default: return 'jpg';
    }
  }
}
