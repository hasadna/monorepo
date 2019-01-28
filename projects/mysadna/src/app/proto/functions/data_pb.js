/**
 * @fileoverview
 * @enhanceable
 * @suppress {messageConventions} JS Compiler reports an error if a variable or
 *     field starts with 'MSG_' and isn't a translatable message.
 * @public
 */
// GENERATED CODE -- DO NOT EDIT!

var jspb = require('google-protobuf');
var goog = jspb;
var global = Function('return this')();

goog.exportSymbol('proto.com.hasadna.mysadna.Contribution', null, global);
goog.exportSymbol('proto.com.hasadna.mysadna.Data', null, global);
goog.exportSymbol('proto.com.hasadna.mysadna.Project', null, global);
goog.exportSymbol('proto.com.hasadna.mysadna.User', null, global);

/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.com.hasadna.mysadna.Contribution = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.com.hasadna.mysadna.Contribution, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.com.hasadna.mysadna.Contribution.displayName = 'proto.com.hasadna.mysadna.Contribution';
}


if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.com.hasadna.mysadna.Contribution.prototype.toObject = function(opt_includeInstance) {
  return proto.com.hasadna.mysadna.Contribution.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.com.hasadna.mysadna.Contribution} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.com.hasadna.mysadna.Contribution.toObject = function(includeInstance, msg) {
  var f, obj = {
    contributionid: jspb.Message.getFieldWithDefault(msg, 1, 0),
    userid: jspb.Message.getFieldWithDefault(msg, 2, 0),
    projectid: jspb.Message.getFieldWithDefault(msg, 3, 0),
    contributionmessage: jspb.Message.getFieldWithDefault(msg, 4, ""),
    contributionurl: jspb.Message.getFieldWithDefault(msg, 5, "")
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.com.hasadna.mysadna.Contribution}
 */
proto.com.hasadna.mysadna.Contribution.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.com.hasadna.mysadna.Contribution;
  return proto.com.hasadna.mysadna.Contribution.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.com.hasadna.mysadna.Contribution} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.com.hasadna.mysadna.Contribution}
 */
proto.com.hasadna.mysadna.Contribution.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = /** @type {number} */ (reader.readInt32());
      msg.setContributionid(value);
      break;
    case 2:
      var value = /** @type {number} */ (reader.readInt32());
      msg.setUserid(value);
      break;
    case 3:
      var value = /** @type {number} */ (reader.readInt32());
      msg.setProjectid(value);
      break;
    case 4:
      var value = /** @type {string} */ (reader.readString());
      msg.setContributionmessage(value);
      break;
    case 5:
      var value = /** @type {string} */ (reader.readString());
      msg.setContributionurl(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.com.hasadna.mysadna.Contribution.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.com.hasadna.mysadna.Contribution.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.com.hasadna.mysadna.Contribution} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.com.hasadna.mysadna.Contribution.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getContributionid();
  if (f !== 0) {
    writer.writeInt32(
      1,
      f
    );
  }
  f = message.getUserid();
  if (f !== 0) {
    writer.writeInt32(
      2,
      f
    );
  }
  f = message.getProjectid();
  if (f !== 0) {
    writer.writeInt32(
      3,
      f
    );
  }
  f = message.getContributionmessage();
  if (f.length > 0) {
    writer.writeString(
      4,
      f
    );
  }
  f = message.getContributionurl();
  if (f.length > 0) {
    writer.writeString(
      5,
      f
    );
  }
};


/**
 * optional int32 contributionId = 1;
 * @return {number}
 */
proto.com.hasadna.mysadna.Contribution.prototype.getContributionid = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 1, 0));
};


/** @param {number} value */
proto.com.hasadna.mysadna.Contribution.prototype.setContributionid = function(value) {
  jspb.Message.setProto3IntField(this, 1, value);
};


/**
 * optional int32 userId = 2;
 * @return {number}
 */
proto.com.hasadna.mysadna.Contribution.prototype.getUserid = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 2, 0));
};


/** @param {number} value */
proto.com.hasadna.mysadna.Contribution.prototype.setUserid = function(value) {
  jspb.Message.setProto3IntField(this, 2, value);
};


/**
 * optional int32 projectId = 3;
 * @return {number}
 */
proto.com.hasadna.mysadna.Contribution.prototype.getProjectid = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 3, 0));
};


/** @param {number} value */
proto.com.hasadna.mysadna.Contribution.prototype.setProjectid = function(value) {
  jspb.Message.setProto3IntField(this, 3, value);
};


/**
 * optional string contributionMessage = 4;
 * @return {string}
 */
proto.com.hasadna.mysadna.Contribution.prototype.getContributionmessage = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 4, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.Contribution.prototype.setContributionmessage = function(value) {
  jspb.Message.setProto3StringField(this, 4, value);
};


/**
 * optional string contributionUrl = 5;
 * @return {string}
 */
proto.com.hasadna.mysadna.Contribution.prototype.getContributionurl = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 5, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.Contribution.prototype.setContributionurl = function(value) {
  jspb.Message.setProto3StringField(this, 5, value);
};



/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.com.hasadna.mysadna.User = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, proto.com.hasadna.mysadna.User.repeatedFields_, null);
};
goog.inherits(proto.com.hasadna.mysadna.User, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.com.hasadna.mysadna.User.displayName = 'proto.com.hasadna.mysadna.User';
}
/**
 * List of repeated fields within this message type.
 * @private {!Array<number>}
 * @const
 */
proto.com.hasadna.mysadna.User.repeatedFields_ = [6,7,8,9];



if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.com.hasadna.mysadna.User.prototype.toObject = function(opt_includeInstance) {
  return proto.com.hasadna.mysadna.User.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.com.hasadna.mysadna.User} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.com.hasadna.mysadna.User.toObject = function(includeInstance, msg) {
  var f, obj = {
    userid: jspb.Message.getFieldWithDefault(msg, 1, 0),
    username: jspb.Message.getFieldWithDefault(msg, 2, ""),
    firstname: jspb.Message.getFieldWithDefault(msg, 3, ""),
    lastname: jspb.Message.getFieldWithDefault(msg, 4, ""),
    email: jspb.Message.getFieldWithDefault(msg, 5, ""),
    socialnetworkList: jspb.Message.getRepeatedField(msg, 6),
    skillList: jspb.Message.getRepeatedField(msg, 7),
    projectList: jspb.Message.getRepeatedField(msg, 8),
    usercontributionList: jspb.Message.toObjectList(msg.getUsercontributionList(),
    proto.com.hasadna.mysadna.Contribution.toObject, includeInstance),
    userimg: jspb.Message.getFieldWithDefault(msg, 10, "")
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.com.hasadna.mysadna.User}
 */
proto.com.hasadna.mysadna.User.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.com.hasadna.mysadna.User;
  return proto.com.hasadna.mysadna.User.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.com.hasadna.mysadna.User} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.com.hasadna.mysadna.User}
 */
proto.com.hasadna.mysadna.User.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = /** @type {number} */ (reader.readInt32());
      msg.setUserid(value);
      break;
    case 2:
      var value = /** @type {string} */ (reader.readString());
      msg.setUsername(value);
      break;
    case 3:
      var value = /** @type {string} */ (reader.readString());
      msg.setFirstname(value);
      break;
    case 4:
      var value = /** @type {string} */ (reader.readString());
      msg.setLastname(value);
      break;
    case 5:
      var value = /** @type {string} */ (reader.readString());
      msg.setEmail(value);
      break;
    case 6:
      var value = /** @type {string} */ (reader.readString());
      msg.addSocialnetwork(value);
      break;
    case 7:
      var value = /** @type {string} */ (reader.readString());
      msg.addSkill(value);
      break;
    case 8:
      var value = /** @type {!Array<number>} */ (reader.readPackedInt32());
      msg.setProjectList(value);
      break;
    case 9:
      var value = new proto.com.hasadna.mysadna.Contribution;
      reader.readMessage(value,proto.com.hasadna.mysadna.Contribution.deserializeBinaryFromReader);
      msg.addUsercontribution(value);
      break;
    case 10:
      var value = /** @type {string} */ (reader.readString());
      msg.setUserimg(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.com.hasadna.mysadna.User.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.com.hasadna.mysadna.User.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.com.hasadna.mysadna.User} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.com.hasadna.mysadna.User.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getUserid();
  if (f !== 0) {
    writer.writeInt32(
      1,
      f
    );
  }
  f = message.getUsername();
  if (f.length > 0) {
    writer.writeString(
      2,
      f
    );
  }
  f = message.getFirstname();
  if (f.length > 0) {
    writer.writeString(
      3,
      f
    );
  }
  f = message.getLastname();
  if (f.length > 0) {
    writer.writeString(
      4,
      f
    );
  }
  f = message.getEmail();
  if (f.length > 0) {
    writer.writeString(
      5,
      f
    );
  }
  f = message.getSocialnetworkList();
  if (f.length > 0) {
    writer.writeRepeatedString(
      6,
      f
    );
  }
  f = message.getSkillList();
  if (f.length > 0) {
    writer.writeRepeatedString(
      7,
      f
    );
  }
  f = message.getProjectList();
  if (f.length > 0) {
    writer.writePackedInt32(
      8,
      f
    );
  }
  f = message.getUsercontributionList();
  if (f.length > 0) {
    writer.writeRepeatedMessage(
      9,
      f,
      proto.com.hasadna.mysadna.Contribution.serializeBinaryToWriter
    );
  }
  f = message.getUserimg();
  if (f.length > 0) {
    writer.writeString(
      10,
      f
    );
  }
};


/**
 * optional int32 userId = 1;
 * @return {number}
 */
proto.com.hasadna.mysadna.User.prototype.getUserid = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 1, 0));
};


/** @param {number} value */
proto.com.hasadna.mysadna.User.prototype.setUserid = function(value) {
  jspb.Message.setProto3IntField(this, 1, value);
};


/**
 * optional string userName = 2;
 * @return {string}
 */
proto.com.hasadna.mysadna.User.prototype.getUsername = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 2, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.User.prototype.setUsername = function(value) {
  jspb.Message.setProto3StringField(this, 2, value);
};


/**
 * optional string firstName = 3;
 * @return {string}
 */
proto.com.hasadna.mysadna.User.prototype.getFirstname = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 3, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.User.prototype.setFirstname = function(value) {
  jspb.Message.setProto3StringField(this, 3, value);
};


/**
 * optional string lastName = 4;
 * @return {string}
 */
proto.com.hasadna.mysadna.User.prototype.getLastname = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 4, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.User.prototype.setLastname = function(value) {
  jspb.Message.setProto3StringField(this, 4, value);
};


/**
 * optional string email = 5;
 * @return {string}
 */
proto.com.hasadna.mysadna.User.prototype.getEmail = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 5, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.User.prototype.setEmail = function(value) {
  jspb.Message.setProto3StringField(this, 5, value);
};


/**
 * repeated string socialNetwork = 6;
 * @return {!Array<string>}
 */
proto.com.hasadna.mysadna.User.prototype.getSocialnetworkList = function() {
  return /** @type {!Array<string>} */ (jspb.Message.getRepeatedField(this, 6));
};


/** @param {!Array<string>} value */
proto.com.hasadna.mysadna.User.prototype.setSocialnetworkList = function(value) {
  jspb.Message.setField(this, 6, value || []);
};


/**
 * @param {!string} value
 * @param {number=} opt_index
 */
proto.com.hasadna.mysadna.User.prototype.addSocialnetwork = function(value, opt_index) {
  jspb.Message.addToRepeatedField(this, 6, value, opt_index);
};


proto.com.hasadna.mysadna.User.prototype.clearSocialnetworkList = function() {
  this.setSocialnetworkList([]);
};


/**
 * repeated string skill = 7;
 * @return {!Array<string>}
 */
proto.com.hasadna.mysadna.User.prototype.getSkillList = function() {
  return /** @type {!Array<string>} */ (jspb.Message.getRepeatedField(this, 7));
};


/** @param {!Array<string>} value */
proto.com.hasadna.mysadna.User.prototype.setSkillList = function(value) {
  jspb.Message.setField(this, 7, value || []);
};


/**
 * @param {!string} value
 * @param {number=} opt_index
 */
proto.com.hasadna.mysadna.User.prototype.addSkill = function(value, opt_index) {
  jspb.Message.addToRepeatedField(this, 7, value, opt_index);
};


proto.com.hasadna.mysadna.User.prototype.clearSkillList = function() {
  this.setSkillList([]);
};


/**
 * repeated int32 project = 8;
 * @return {!Array<number>}
 */
proto.com.hasadna.mysadna.User.prototype.getProjectList = function() {
  return /** @type {!Array<number>} */ (jspb.Message.getRepeatedField(this, 8));
};


/** @param {!Array<number>} value */
proto.com.hasadna.mysadna.User.prototype.setProjectList = function(value) {
  jspb.Message.setField(this, 8, value || []);
};


/**
 * @param {!number} value
 * @param {number=} opt_index
 */
proto.com.hasadna.mysadna.User.prototype.addProject = function(value, opt_index) {
  jspb.Message.addToRepeatedField(this, 8, value, opt_index);
};


proto.com.hasadna.mysadna.User.prototype.clearProjectList = function() {
  this.setProjectList([]);
};


/**
 * repeated Contribution userContribution = 9;
 * @return {!Array<!proto.com.hasadna.mysadna.Contribution>}
 */
proto.com.hasadna.mysadna.User.prototype.getUsercontributionList = function() {
  return /** @type{!Array<!proto.com.hasadna.mysadna.Contribution>} */ (
    jspb.Message.getRepeatedWrapperField(this, proto.com.hasadna.mysadna.Contribution, 9));
};


/** @param {!Array<!proto.com.hasadna.mysadna.Contribution>} value */
proto.com.hasadna.mysadna.User.prototype.setUsercontributionList = function(value) {
  jspb.Message.setRepeatedWrapperField(this, 9, value);
};


/**
 * @param {!proto.com.hasadna.mysadna.Contribution=} opt_value
 * @param {number=} opt_index
 * @return {!proto.com.hasadna.mysadna.Contribution}
 */
proto.com.hasadna.mysadna.User.prototype.addUsercontribution = function(opt_value, opt_index) {
  return jspb.Message.addToRepeatedWrapperField(this, 9, opt_value, proto.com.hasadna.mysadna.Contribution, opt_index);
};


proto.com.hasadna.mysadna.User.prototype.clearUsercontributionList = function() {
  this.setUsercontributionList([]);
};


/**
 * optional string userImg = 10;
 * @return {string}
 */
proto.com.hasadna.mysadna.User.prototype.getUserimg = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 10, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.User.prototype.setUserimg = function(value) {
  jspb.Message.setProto3StringField(this, 10, value);
};



/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.com.hasadna.mysadna.Project = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, proto.com.hasadna.mysadna.Project.repeatedFields_, null);
};
goog.inherits(proto.com.hasadna.mysadna.Project, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.com.hasadna.mysadna.Project.displayName = 'proto.com.hasadna.mysadna.Project';
}
/**
 * List of repeated fields within this message type.
 * @private {!Array<number>}
 * @const
 */
proto.com.hasadna.mysadna.Project.repeatedFields_ = [5,6];



if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.com.hasadna.mysadna.Project.prototype.toObject = function(opt_includeInstance) {
  return proto.com.hasadna.mysadna.Project.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.com.hasadna.mysadna.Project} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.com.hasadna.mysadna.Project.toObject = function(includeInstance, msg) {
  var f, obj = {
    projectid: jspb.Message.getFieldWithDefault(msg, 1, 0),
    projectname: jspb.Message.getFieldWithDefault(msg, 2, ""),
    projectdescription: jspb.Message.getFieldWithDefault(msg, 3, ""),
    projectsite: jspb.Message.getFieldWithDefault(msg, 4, ""),
    contributorList: jspb.Message.getRepeatedField(msg, 5),
    projectcontributionList: jspb.Message.toObjectList(msg.getProjectcontributionList(),
    proto.com.hasadna.mysadna.Contribution.toObject, includeInstance)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.com.hasadna.mysadna.Project}
 */
proto.com.hasadna.mysadna.Project.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.com.hasadna.mysadna.Project;
  return proto.com.hasadna.mysadna.Project.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.com.hasadna.mysadna.Project} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.com.hasadna.mysadna.Project}
 */
proto.com.hasadna.mysadna.Project.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = /** @type {number} */ (reader.readInt32());
      msg.setProjectid(value);
      break;
    case 2:
      var value = /** @type {string} */ (reader.readString());
      msg.setProjectname(value);
      break;
    case 3:
      var value = /** @type {string} */ (reader.readString());
      msg.setProjectdescription(value);
      break;
    case 4:
      var value = /** @type {string} */ (reader.readString());
      msg.setProjectsite(value);
      break;
    case 5:
      var value = /** @type {!Array<number>} */ (reader.readPackedInt32());
      msg.setContributorList(value);
      break;
    case 6:
      var value = new proto.com.hasadna.mysadna.Contribution;
      reader.readMessage(value,proto.com.hasadna.mysadna.Contribution.deserializeBinaryFromReader);
      msg.addProjectcontribution(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.com.hasadna.mysadna.Project.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.com.hasadna.mysadna.Project.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.com.hasadna.mysadna.Project} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.com.hasadna.mysadna.Project.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getProjectid();
  if (f !== 0) {
    writer.writeInt32(
      1,
      f
    );
  }
  f = message.getProjectname();
  if (f.length > 0) {
    writer.writeString(
      2,
      f
    );
  }
  f = message.getProjectdescription();
  if (f.length > 0) {
    writer.writeString(
      3,
      f
    );
  }
  f = message.getProjectsite();
  if (f.length > 0) {
    writer.writeString(
      4,
      f
    );
  }
  f = message.getContributorList();
  if (f.length > 0) {
    writer.writePackedInt32(
      5,
      f
    );
  }
  f = message.getProjectcontributionList();
  if (f.length > 0) {
    writer.writeRepeatedMessage(
      6,
      f,
      proto.com.hasadna.mysadna.Contribution.serializeBinaryToWriter
    );
  }
};


/**
 * optional int32 projectId = 1;
 * @return {number}
 */
proto.com.hasadna.mysadna.Project.prototype.getProjectid = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 1, 0));
};


/** @param {number} value */
proto.com.hasadna.mysadna.Project.prototype.setProjectid = function(value) {
  jspb.Message.setProto3IntField(this, 1, value);
};


/**
 * optional string projectName = 2;
 * @return {string}
 */
proto.com.hasadna.mysadna.Project.prototype.getProjectname = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 2, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.Project.prototype.setProjectname = function(value) {
  jspb.Message.setProto3StringField(this, 2, value);
};


/**
 * optional string projectDescription = 3;
 * @return {string}
 */
proto.com.hasadna.mysadna.Project.prototype.getProjectdescription = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 3, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.Project.prototype.setProjectdescription = function(value) {
  jspb.Message.setProto3StringField(this, 3, value);
};


/**
 * optional string projectSite = 4;
 * @return {string}
 */
proto.com.hasadna.mysadna.Project.prototype.getProjectsite = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 4, ""));
};


/** @param {string} value */
proto.com.hasadna.mysadna.Project.prototype.setProjectsite = function(value) {
  jspb.Message.setProto3StringField(this, 4, value);
};


/**
 * repeated int32 contributor = 5;
 * @return {!Array<number>}
 */
proto.com.hasadna.mysadna.Project.prototype.getContributorList = function() {
  return /** @type {!Array<number>} */ (jspb.Message.getRepeatedField(this, 5));
};


/** @param {!Array<number>} value */
proto.com.hasadna.mysadna.Project.prototype.setContributorList = function(value) {
  jspb.Message.setField(this, 5, value || []);
};


/**
 * @param {!number} value
 * @param {number=} opt_index
 */
proto.com.hasadna.mysadna.Project.prototype.addContributor = function(value, opt_index) {
  jspb.Message.addToRepeatedField(this, 5, value, opt_index);
};


proto.com.hasadna.mysadna.Project.prototype.clearContributorList = function() {
  this.setContributorList([]);
};


/**
 * repeated Contribution projectContribution = 6;
 * @return {!Array<!proto.com.hasadna.mysadna.Contribution>}
 */
proto.com.hasadna.mysadna.Project.prototype.getProjectcontributionList = function() {
  return /** @type{!Array<!proto.com.hasadna.mysadna.Contribution>} */ (
    jspb.Message.getRepeatedWrapperField(this, proto.com.hasadna.mysadna.Contribution, 6));
};


/** @param {!Array<!proto.com.hasadna.mysadna.Contribution>} value */
proto.com.hasadna.mysadna.Project.prototype.setProjectcontributionList = function(value) {
  jspb.Message.setRepeatedWrapperField(this, 6, value);
};


/**
 * @param {!proto.com.hasadna.mysadna.Contribution=} opt_value
 * @param {number=} opt_index
 * @return {!proto.com.hasadna.mysadna.Contribution}
 */
proto.com.hasadna.mysadna.Project.prototype.addProjectcontribution = function(opt_value, opt_index) {
  return jspb.Message.addToRepeatedWrapperField(this, 6, opt_value, proto.com.hasadna.mysadna.Contribution, opt_index);
};


proto.com.hasadna.mysadna.Project.prototype.clearProjectcontributionList = function() {
  this.setProjectcontributionList([]);
};



/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.com.hasadna.mysadna.Data = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, proto.com.hasadna.mysadna.Data.repeatedFields_, null);
};
goog.inherits(proto.com.hasadna.mysadna.Data, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.com.hasadna.mysadna.Data.displayName = 'proto.com.hasadna.mysadna.Data';
}
/**
 * List of repeated fields within this message type.
 * @private {!Array<number>}
 * @const
 */
proto.com.hasadna.mysadna.Data.repeatedFields_ = [1,2];



if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.com.hasadna.mysadna.Data.prototype.toObject = function(opt_includeInstance) {
  return proto.com.hasadna.mysadna.Data.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.com.hasadna.mysadna.Data} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.com.hasadna.mysadna.Data.toObject = function(includeInstance, msg) {
  var f, obj = {
    userList: jspb.Message.toObjectList(msg.getUserList(),
    proto.com.hasadna.mysadna.User.toObject, includeInstance),
    projectList: jspb.Message.toObjectList(msg.getProjectList(),
    proto.com.hasadna.mysadna.Project.toObject, includeInstance)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.com.hasadna.mysadna.Data}
 */
proto.com.hasadna.mysadna.Data.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.com.hasadna.mysadna.Data;
  return proto.com.hasadna.mysadna.Data.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.com.hasadna.mysadna.Data} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.com.hasadna.mysadna.Data}
 */
proto.com.hasadna.mysadna.Data.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = new proto.com.hasadna.mysadna.User;
      reader.readMessage(value,proto.com.hasadna.mysadna.User.deserializeBinaryFromReader);
      msg.addUser(value);
      break;
    case 2:
      var value = new proto.com.hasadna.mysadna.Project;
      reader.readMessage(value,proto.com.hasadna.mysadna.Project.deserializeBinaryFromReader);
      msg.addProject(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.com.hasadna.mysadna.Data.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.com.hasadna.mysadna.Data.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.com.hasadna.mysadna.Data} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.com.hasadna.mysadna.Data.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getUserList();
  if (f.length > 0) {
    writer.writeRepeatedMessage(
      1,
      f,
      proto.com.hasadna.mysadna.User.serializeBinaryToWriter
    );
  }
  f = message.getProjectList();
  if (f.length > 0) {
    writer.writeRepeatedMessage(
      2,
      f,
      proto.com.hasadna.mysadna.Project.serializeBinaryToWriter
    );
  }
};


/**
 * repeated User user = 1;
 * @return {!Array<!proto.com.hasadna.mysadna.User>}
 */
proto.com.hasadna.mysadna.Data.prototype.getUserList = function() {
  return /** @type{!Array<!proto.com.hasadna.mysadna.User>} */ (
    jspb.Message.getRepeatedWrapperField(this, proto.com.hasadna.mysadna.User, 1));
};


/** @param {!Array<!proto.com.hasadna.mysadna.User>} value */
proto.com.hasadna.mysadna.Data.prototype.setUserList = function(value) {
  jspb.Message.setRepeatedWrapperField(this, 1, value);
};


/**
 * @param {!proto.com.hasadna.mysadna.User=} opt_value
 * @param {number=} opt_index
 * @return {!proto.com.hasadna.mysadna.User}
 */
proto.com.hasadna.mysadna.Data.prototype.addUser = function(opt_value, opt_index) {
  return jspb.Message.addToRepeatedWrapperField(this, 1, opt_value, proto.com.hasadna.mysadna.User, opt_index);
};


proto.com.hasadna.mysadna.Data.prototype.clearUserList = function() {
  this.setUserList([]);
};


/**
 * repeated Project project = 2;
 * @return {!Array<!proto.com.hasadna.mysadna.Project>}
 */
proto.com.hasadna.mysadna.Data.prototype.getProjectList = function() {
  return /** @type{!Array<!proto.com.hasadna.mysadna.Project>} */ (
    jspb.Message.getRepeatedWrapperField(this, proto.com.hasadna.mysadna.Project, 2));
};


/** @param {!Array<!proto.com.hasadna.mysadna.Project>} value */
proto.com.hasadna.mysadna.Data.prototype.setProjectList = function(value) {
  jspb.Message.setRepeatedWrapperField(this, 2, value);
};


/**
 * @param {!proto.com.hasadna.mysadna.Project=} opt_value
 * @param {number=} opt_index
 * @return {!proto.com.hasadna.mysadna.Project}
 */
proto.com.hasadna.mysadna.Data.prototype.addProject = function(opt_value, opt_index) {
  return jspb.Message.addToRepeatedWrapperField(this, 2, opt_value, proto.com.hasadna.mysadna.Project, opt_index);
};


proto.com.hasadna.mysadna.Data.prototype.clearProjectList = function() {
  this.setProjectList([]);
};


goog.object.extend(exports, proto.com.hasadna.mysadna);
