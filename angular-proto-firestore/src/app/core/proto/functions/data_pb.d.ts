// package: com.hasadna.protobin_reader
// file: data.proto

import * as jspb from "google-protobuf";

export class ShareStoriesRequest extends jspb.Message {
  hasStories(): boolean;
  clearStories(): void;
  getStories(): StoryList | undefined;
  setStories(value?: StoryList): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ShareStoriesRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ShareStoriesRequest): ShareStoriesRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ShareStoriesRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ShareStoriesRequest;
  static deserializeBinaryFromReader(message: ShareStoriesRequest, reader: jspb.BinaryReader): ShareStoriesRequest;
}

export namespace ShareStoriesRequest {
  export type AsObject = {
    stories?: StoryList.AsObject,
  }
}

export class ShareStoriesResponse extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ShareStoriesResponse.AsObject;
  static toObject(includeInstance: boolean, msg: ShareStoriesResponse): ShareStoriesResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ShareStoriesResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ShareStoriesResponse;
  static deserializeBinaryFromReader(message: ShareStoriesResponse, reader: jspb.BinaryReader): ShareStoriesResponse;
}

export namespace ShareStoriesResponse {
  export type AsObject = {
  }
}

export class StoryItem extends jspb.Message {
  getTimems(): number;
  setTimems(value: number): void;

  getScreenshot(): Uint8Array | string;
  getScreenshot_asU8(): Uint8Array;
  getScreenshot_asB64(): string;
  setScreenshot(value: Uint8Array | string): void;

  getOneliner(): string;
  setOneliner(value: string): void;

  getNote(): string;
  setNote(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): StoryItem.AsObject;
  static toObject(includeInstance: boolean, msg: StoryItem): StoryItem.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: StoryItem, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): StoryItem;
  static deserializeBinaryFromReader(message: StoryItem, reader: jspb.BinaryReader): StoryItem;
}

export namespace StoryItem {
  export type AsObject = {
    timems: number,
    screenshot: Uint8Array | string,
    oneliner: string,
    note: string,
  }
}

export class Story extends jspb.Message {
  getStarttimems(): number;
  setStarttimems(value: number): void;

  getEndtimems(): number;
  setEndtimems(value: number): void;

  getProject(): string;
  setProject(value: string): void;

  clearItemList(): void;
  getItemList(): Array<StoryItem>;
  setItemList(value: Array<StoryItem>): void;
  addItem(value?: StoryItem, index?: number): StoryItem;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Story.AsObject;
  static toObject(includeInstance: boolean, msg: Story): Story.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Story, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Story;
  static deserializeBinaryFromReader(message: Story, reader: jspb.BinaryReader): Story;
}

export namespace Story {
  export type AsObject = {
    starttimems: number,
    endtimems: number,
    project: string,
    itemList: Array<StoryItem.AsObject>,
  }
}

export class StoryList extends jspb.Message {
  clearStoryList(): void;
  getStoryList(): Array<Story>;
  setStoryList(value: Array<Story>): void;
  addStory(value?: Story, index?: number): Story;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): StoryList.AsObject;
  static toObject(includeInstance: boolean, msg: StoryList): StoryList.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: StoryList, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): StoryList;
  static deserializeBinaryFromReader(message: StoryList, reader: jspb.BinaryReader): StoryList;
}

export namespace StoryList {
  export type AsObject = {
    storyList: Array<Story.AsObject>,
  }
}

