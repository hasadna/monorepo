// package: com.hasadna.protobin_reader
// file: data.proto

import * as jspb from "google-protobuf";

export class Contribution extends jspb.Message {
  getContributionid(): number;
  setContributionid(value: number): void;

  getUserid(): number;
  setUserid(value: number): void;

  getProjectid(): number;
  setProjectid(value: number): void;

  getContributionmessage(): string;
  setContributionmessage(value: string): void;

  getContributionurl(): string;
  setContributionurl(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Contribution.AsObject;
  static toObject(includeInstance: boolean, msg: Contribution): Contribution.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Contribution, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Contribution;
  static deserializeBinaryFromReader(message: Contribution, reader: jspb.BinaryReader): Contribution;
}

export namespace Contribution {
  export type AsObject = {
    contributionid: number,
    userid: number,
    projectid: number,
    contributionmessage: string,
    contributionurl: string,
  }
}

export class User extends jspb.Message {
  getUserid(): number;
  setUserid(value: number): void;

  getUsername(): string;
  setUsername(value: string): void;

  getFirstname(): string;
  setFirstname(value: string): void;

  getLastname(): string;
  setLastname(value: string): void;

  getEmail(): string;
  setEmail(value: string): void;

  clearSocialnetworkList(): void;
  getSocialnetworkList(): Array<string>;
  setSocialnetworkList(value: Array<string>): void;
  addSocialnetwork(value: string, index?: number): string;

  clearSkillList(): void;
  getSkillList(): Array<string>;
  setSkillList(value: Array<string>): void;
  addSkill(value: string, index?: number): string;

  clearProjectList(): void;
  getProjectList(): Array<number>;
  setProjectList(value: Array<number>): void;
  addProject(value: number, index?: number): number;

  clearUsercontributionList(): void;
  getUsercontributionList(): Array<Contribution>;
  setUsercontributionList(value: Array<Contribution>): void;
  addUsercontribution(value?: Contribution, index?: number): Contribution;

  getUserimg(): string;
  setUserimg(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): User.AsObject;
  static toObject(includeInstance: boolean, msg: User): User.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: User, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): User;
  static deserializeBinaryFromReader(message: User, reader: jspb.BinaryReader): User;
}

export namespace User {
  export type AsObject = {
    userid: number,
    username: string,
    firstname: string,
    lastname: string,
    email: string,
    socialnetworkList: Array<string>,
    skillList: Array<string>,
    projectList: Array<number>,
    usercontributionList: Array<Contribution.AsObject>,
    userimg: string,
  }
}

export class Project extends jspb.Message {
  getProjectid(): number;
  setProjectid(value: number): void;

  getProjectname(): string;
  setProjectname(value: string): void;

  getProjectdescription(): string;
  setProjectdescription(value: string): void;

  getProjectsite(): string;
  setProjectsite(value: string): void;

  clearContributorList(): void;
  getContributorList(): Array<number>;
  setContributorList(value: Array<number>): void;
  addContributor(value: number, index?: number): number;

  clearProjectcontributionList(): void;
  getProjectcontributionList(): Array<string>;
  setProjectcontributionList(value: Array<string>): void;
  addProjectcontribution(value: string, index?: number): string;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Project.AsObject;
  static toObject(includeInstance: boolean, msg: Project): Project.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Project, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Project;
  static deserializeBinaryFromReader(message: Project, reader: jspb.BinaryReader): Project;
}

export namespace Project {
  export type AsObject = {
    projectid: number,
    projectname: string,
    projectdescription: string,
    projectsite: string,
    contributorList: Array<number>,
    projectcontributionList: Array<string>,
  }
}

export class Data extends jspb.Message {
  clearUserList(): void;
  getUserList(): Array<User>;
  setUserList(value: Array<User>): void;
  addUser(value?: User, index?: number): User;

  clearProjectList(): void;
  getProjectList(): Array<Project>;
  setProjectList(value: Array<Project>): void;
  addProject(value?: Project, index?: number): Project;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Data.AsObject;
  static toObject(includeInstance: boolean, msg: Data): Data.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Data, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Data;
  static deserializeBinaryFromReader(message: Data, reader: jspb.BinaryReader): Data;
}

export namespace Data {
  export type AsObject = {
    userList: Array<User.AsObject>,
    projectList: Array<Project.AsObject>,
  }
}

