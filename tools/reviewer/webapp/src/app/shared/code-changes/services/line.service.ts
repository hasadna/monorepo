import { Injectable } from '@angular/core';

import { DiffLine } from '@/core/proto';
import {
  BlockIndex,
  BlockLine,
  ChangesLine,
  Dictionary,
} from '../code-changes.interface';

// Constructors of line interfaces
@Injectable()
export class LineService {
  createEmptyBlockLine(): BlockLine {
    return {
      code: '',
      clearCode: '',
      wordChanges: '',
      lineNumber: 0,
      isChanged: false,
      isPlaceholder: false,
      threads: [],
      diffLine: new DiffLine(),
    };
  }

  createPlaceholder(): BlockLine {
    const placeholder: BlockLine = this.createEmptyBlockLine();
    placeholder.isPlaceholder = true;
    return placeholder;
  }

  createBlockLine(
    code: string,
    clearCode: string,
    lineNumber: number,
  ): BlockLine {
    const blockLine: BlockLine = this.createEmptyBlockLine();
    blockLine.code = code;
    blockLine.clearCode = clearCode;
    blockLine.lineNumber = lineNumber;
    return blockLine;
  }

  createEmptyChangesLine(): ChangesLine {
    return {
      blocks: [this.createPlaceholder(), this.createPlaceholder()],
      isCommentsLine: false,
      commentsLine: undefined,
    };
  }

  createChangesLine(
    leftBlockLine: BlockLine,
    rightBlockLine: BlockLine,
  ): ChangesLine {
    const changesLine: ChangesLine = this.createEmptyChangesLine();
    changesLine.blocks = [leftBlockLine, rightBlockLine];
    return changesLine;
  }

  createCommentsLine(
    leftBlockLine: BlockLine,
    rightBlockLine: BlockLine,
  ): ChangesLine {
    const commentsLine: ChangesLine = this.createEmptyChangesLine();
    commentsLine.isCommentsLine = true;
    commentsLine.commentsLine = commentsLine;
    commentsLine.blocks[BlockIndex.leftFile].lineNumber = leftBlockLine
      .lineNumber;
    commentsLine.blocks[BlockIndex.rightFile].lineNumber = rightBlockLine
      .lineNumber;
    return commentsLine;
  }

  // Split dictionary is a matrix, where
  // first key is index of block (left or right)
  // second key is line number of block.
  // By the second key you can get fast access to lineIndex (line number of code changes)
  createSplitDictionary(): Dictionary[] {
    const splitDictionary: Dictionary[] = [];
    splitDictionary[BlockIndex.leftFile] = {};
    splitDictionary[BlockIndex.rightFile] = {};

    return splitDictionary;
  }
}
