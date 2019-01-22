// package: com.hasadna.protobin_reader
// file: data.proto

import * as jspb from "google-protobuf";

export class Config extends jspb.Message {
  getScreenshotFrequency(): Config.Frequency;
  setScreenshotFrequency(value: Config.Frequency): void;

  getStoriesPath(): string;
  setStoriesPath(value: string): void;

  getInvoicesPath(): string;
  setInvoicesPath(value: string): void;

  hasInvoiceCreator(): boolean;
  clearInvoiceCreator(): void;
  getInvoiceCreator(): InvoiceCreator | undefined;
  setInvoiceCreator(value?: InvoiceCreator): void;

  hasInvoiceReceiver(): boolean;
  clearInvoiceReceiver(): void;
  getInvoiceReceiver(): InvoiceReceiver | undefined;
  setInvoiceReceiver(value?: InvoiceReceiver): void;

  clearProjectsList(): void;
  getProjectsList(): Array<string>;
  setProjectsList(value: Array<string>): void;
  addProjects(value: string, index?: number): string;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Config.AsObject;
  static toObject(includeInstance: boolean, msg: Config): Config.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Config, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Config;
  static deserializeBinaryFromReader(message: Config, reader: jspb.BinaryReader): Config;
}

export namespace Config {
  export type AsObject = {
    screenshotFrequency: Config.Frequency,
    storiesPath: string,
    invoicesPath: string,
    invoiceCreator?: InvoiceCreator.AsObject,
    invoiceReceiver?: InvoiceReceiver.AsObject,
    projectsList: Array<string>,
  }

  export enum Frequency {
    EVERY_10_MINUTES = 0,
    EVERY_15_MINUTES = 1,
    EVERY_20_MINUTES = 2,
  }
}

export class UiDefaults extends jspb.Message {
  clearProjectList(): void;
  getProjectList(): Array<string>;
  setProjectList(value: Array<string>): void;
  addProject(value: string, index?: number): string;

  getLeft(): number;
  setLeft(value: number): void;

  getTop(): number;
  setTop(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UiDefaults.AsObject;
  static toObject(includeInstance: boolean, msg: UiDefaults): UiDefaults.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: UiDefaults, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UiDefaults;
  static deserializeBinaryFromReader(message: UiDefaults, reader: jspb.BinaryReader): UiDefaults;
}

export namespace UiDefaults {
  export type AsObject = {
    projectList: Array<string>,
    left: number,
    top: number,
  }
}

export class StatusData extends jspb.Message {
  getProject(): string;
  setProject(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): StatusData.AsObject;
  static toObject(includeInstance: boolean, msg: StatusData): StatusData.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: StatusData, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): StatusData;
  static deserializeBinaryFromReader(message: StatusData, reader: jspb.BinaryReader): StatusData;
}

export namespace StatusData {
  export type AsObject = {
    project: string,
  }
}

export class ScreenshotMetadata extends jspb.Message {
  getProject(): string;
  setProject(value: string): void;

  getOneliner(): string;
  setOneliner(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ScreenshotMetadata.AsObject;
  static toObject(includeInstance: boolean, msg: ScreenshotMetadata): ScreenshotMetadata.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ScreenshotMetadata, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ScreenshotMetadata;
  static deserializeBinaryFromReader(message: ScreenshotMetadata, reader: jspb.BinaryReader): ScreenshotMetadata;
}

export namespace ScreenshotMetadata {
  export type AsObject = {
    project: string,
    oneliner: string,
  }
}

export class FileData extends jspb.Message {
  getType(): FileData.Type;
  setType(value: FileData.Type): void;

  getTimems(): number;
  setTimems(value: number): void;

  getFilename(): string;
  setFilename(value: string): void;

  hasImageBytes(): boolean;
  clearImageBytes(): void;
  getImageBytes(): Uint8Array | string;
  getImageBytes_asU8(): Uint8Array;
  getImageBytes_asB64(): string;
  setImageBytes(value: Uint8Array | string): void;

  hasScreenshotMetadata(): boolean;
  clearScreenshotMetadata(): void;
  getScreenshotMetadata(): ScreenshotMetadata | undefined;
  setScreenshotMetadata(value?: ScreenshotMetadata): void;

  hasStatusData(): boolean;
  clearStatusData(): void;
  getStatusData(): StatusData | undefined;
  setStatusData(value?: StatusData): void;

  getOneofContentCase(): FileData.OneofContentCase;
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): FileData.AsObject;
  static toObject(includeInstance: boolean, msg: FileData): FileData.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: FileData, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): FileData;
  static deserializeBinaryFromReader(message: FileData, reader: jspb.BinaryReader): FileData;
}

export namespace FileData {
  export type AsObject = {
    type: FileData.Type,
    timems: number,
    filename: string,
    imageBytes: Uint8Array | string,
    screenshotMetadata?: ScreenshotMetadata.AsObject,
    statusData?: StatusData.AsObject,
  }

  export enum Type {
    UNKNOWN = 0,
    START = 1,
    RUNNING = 2,
    SCREENSHOT = 3,
    SCREENSHOT_METADATA = 4,
    END = 5,
  }

  export enum OneofContentCase {
    ONEOF_CONTENT_NOT_SET = 0,
    IMAGE_BYTES = 4,
    SCREENSHOT_METADATA = 5,
    STATUS_DATA = 6,
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

export class InvoiceItem extends jspb.Message {
  getTitle(): string;
  setTitle(value: string): void;

  getDescription(): string;
  setDescription(value: string): void;

  getTimeWorkedMs(): number;
  setTimeWorkedMs(value: number): void;

  getHourlyRate(): number;
  setHourlyRate(value: number): void;

  getDollaramount(): number;
  setDollaramount(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): InvoiceItem.AsObject;
  static toObject(includeInstance: boolean, msg: InvoiceItem): InvoiceItem.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: InvoiceItem, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): InvoiceItem;
  static deserializeBinaryFromReader(message: InvoiceItem, reader: jspb.BinaryReader): InvoiceItem;
}

export namespace InvoiceItem {
  export type AsObject = {
    title: string,
    description: string,
    timeWorkedMs: number,
    hourlyRate: number,
    dollaramount: number,
  }
}

export class InvoiceItemList extends jspb.Message {
  clearItemList(): void;
  getItemList(): Array<InvoiceItem>;
  setItemList(value: Array<InvoiceItem>): void;
  addItem(value?: InvoiceItem, index?: number): InvoiceItem;

  getStarttimems(): number;
  setStarttimems(value: number): void;

  getEndtimems(): number;
  setEndtimems(value: number): void;

  getSubtotal(): number;
  setSubtotal(value: number): void;

  getTotal(): number;
  setTotal(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): InvoiceItemList.AsObject;
  static toObject(includeInstance: boolean, msg: InvoiceItemList): InvoiceItemList.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: InvoiceItemList, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): InvoiceItemList;
  static deserializeBinaryFromReader(message: InvoiceItemList, reader: jspb.BinaryReader): InvoiceItemList;
}

export namespace InvoiceItemList {
  export type AsObject = {
    itemList: Array<InvoiceItem.AsObject>,
    starttimems: number,
    endtimems: number,
    subtotal: number,
    total: number,
  }
}

export class InvoiceCreator extends jspb.Message {
  getName(): string;
  setName(value: string): void;

  getPhoneNumber(): string;
  setPhoneNumber(value: string): void;

  getEmail(): string;
  setEmail(value: string): void;

  getWebsite(): string;
  setWebsite(value: string): void;

  getStreetAndNumber(): string;
  setStreetAndNumber(value: string): void;

  getCityStateCountry(): string;
  setCityStateCountry(value: string): void;

  getZipCode(): string;
  setZipCode(value: string): void;

  getTagline(): string;
  setTagline(value: string): void;

  getHourlyRateUsd(): number;
  setHourlyRateUsd(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): InvoiceCreator.AsObject;
  static toObject(includeInstance: boolean, msg: InvoiceCreator): InvoiceCreator.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: InvoiceCreator, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): InvoiceCreator;
  static deserializeBinaryFromReader(message: InvoiceCreator, reader: jspb.BinaryReader): InvoiceCreator;
}

export namespace InvoiceCreator {
  export type AsObject = {
    name: string,
    phoneNumber: string,
    email: string,
    website: string,
    streetAndNumber: string,
    cityStateCountry: string,
    zipCode: string,
    tagline: string,
    hourlyRateUsd: number,
  }
}

export class InvoiceReceiver extends jspb.Message {
  getName(): string;
  setName(value: string): void;

  getStreetAndNumber(): string;
  setStreetAndNumber(value: string): void;

  getCityStateCountry(): string;
  setCityStateCountry(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): InvoiceReceiver.AsObject;
  static toObject(includeInstance: boolean, msg: InvoiceReceiver): InvoiceReceiver.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: InvoiceReceiver, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): InvoiceReceiver;
  static deserializeBinaryFromReader(message: InvoiceReceiver, reader: jspb.BinaryReader): InvoiceReceiver;
}

export namespace InvoiceReceiver {
  export type AsObject = {
    name: string,
    streetAndNumber: string,
    cityStateCountry: string,
  }
}

