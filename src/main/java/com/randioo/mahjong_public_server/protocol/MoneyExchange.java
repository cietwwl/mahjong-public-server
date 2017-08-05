// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MoneyExchange.proto

package com.randioo.mahjong_public_server.protocol;

public final class MoneyExchange {
  private MoneyExchange() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class MoneyExchangeRequest extends
      com.google.protobuf.GeneratedMessage {
    // Use MoneyExchangeRequest.newBuilder() to construct.
    private MoneyExchangeRequest() {
      initFields();
    }
    private MoneyExchangeRequest(boolean noInit) {}
    
    private static final MoneyExchangeRequest defaultInstance;
    public static MoneyExchangeRequest getDefaultInstance() {
      return defaultInstance;
    }
    
    public MoneyExchangeRequest getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.randioo.mahjong_public_server.protocol.MoneyExchange.internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeRequest_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.randioo.mahjong_public_server.protocol.MoneyExchange.internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeRequest_fieldAccessorTable;
    }
    
    // optional int32 num = 1;
    public static final int NUM_FIELD_NUMBER = 1;
    private boolean hasNum;
    private int num_ = 0;
    public boolean hasNum() { return hasNum; }
    public int getNum() { return num_; }
    
    // optional bool add = 2;
    public static final int ADD_FIELD_NUMBER = 2;
    private boolean hasAdd;
    private boolean add_ = false;
    public boolean hasAdd() { return hasAdd; }
    public boolean getAdd() { return add_; }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasNum()) {
        output.writeInt32(1, getNum());
      }
      if (hasAdd()) {
        output.writeBool(2, getAdd());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasNum()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, getNum());
      }
      if (hasAdd()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(2, getAdd());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest result;
      
      // Construct using com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest();
        return builder;
      }
      
      protected com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest.getDescriptor();
      }
      
      public com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest getDefaultInstanceForType() {
        return com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest) {
          return mergeFrom((com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest other) {
        if (other == com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest.getDefaultInstance()) return this;
        if (other.hasNum()) {
          setNum(other.getNum());
        }
        if (other.hasAdd()) {
          setAdd(other.getAdd());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setNum(input.readInt32());
              break;
            }
            case 16: {
              setAdd(input.readBool());
              break;
            }
          }
        }
      }
      
      
      // optional int32 num = 1;
      public boolean hasNum() {
        return result.hasNum();
      }
      public int getNum() {
        return result.getNum();
      }
      public Builder setNum(int value) {
        result.hasNum = true;
        result.num_ = value;
        return this;
      }
      public Builder clearNum() {
        result.hasNum = false;
        result.num_ = 0;
        return this;
      }
      
      // optional bool add = 2;
      public boolean hasAdd() {
        return result.hasAdd();
      }
      public boolean getAdd() {
        return result.getAdd();
      }
      public Builder setAdd(boolean value) {
        result.hasAdd = true;
        result.add_ = value;
        return this;
      }
      public Builder clearAdd() {
        result.hasAdd = false;
        result.add_ = false;
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:com.randioo.mahjong_public_server.protocol.MoneyExchangeRequest)
    }
    
    static {
      defaultInstance = new MoneyExchangeRequest(true);
      com.randioo.mahjong_public_server.protocol.MoneyExchange.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.randioo.mahjong_public_server.protocol.MoneyExchangeRequest)
  }
  
  public static final class MoneyExchangeResponse extends
      com.google.protobuf.GeneratedMessage {
    // Use MoneyExchangeResponse.newBuilder() to construct.
    private MoneyExchangeResponse() {
      initFields();
    }
    private MoneyExchangeResponse(boolean noInit) {}
    
    private static final MoneyExchangeResponse defaultInstance;
    public static MoneyExchangeResponse getDefaultInstance() {
      return defaultInstance;
    }
    
    public MoneyExchangeResponse getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.randioo.mahjong_public_server.protocol.MoneyExchange.internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeResponse_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.randioo.mahjong_public_server.protocol.MoneyExchange.internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeResponse_fieldAccessorTable;
    }
    
    // optional int32 errorCode = 1 [default = 1];
    public static final int ERRORCODE_FIELD_NUMBER = 1;
    private boolean hasErrorCode;
    private int errorCode_ = 1;
    public boolean hasErrorCode() { return hasErrorCode; }
    public int getErrorCode() { return errorCode_; }
    
    // optional .com.randioo.mahjong_public_server.protocol.RoleData roleData = 2;
    public static final int ROLEDATA_FIELD_NUMBER = 2;
    private boolean hasRoleData;
    private com.randioo.mahjong_public_server.protocol.Entity.RoleData roleData_;
    public boolean hasRoleData() { return hasRoleData; }
    public com.randioo.mahjong_public_server.protocol.Entity.RoleData getRoleData() { return roleData_; }
    
    private void initFields() {
      roleData_ = com.randioo.mahjong_public_server.protocol.Entity.RoleData.getDefaultInstance();
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasErrorCode()) {
        output.writeInt32(1, getErrorCode());
      }
      if (hasRoleData()) {
        output.writeMessage(2, getRoleData());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasErrorCode()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, getErrorCode());
      }
      if (hasRoleData()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getRoleData());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse result;
      
      // Construct using com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse();
        return builder;
      }
      
      protected com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse.getDescriptor();
      }
      
      public com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse getDefaultInstanceForType() {
        return com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse) {
          return mergeFrom((com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse other) {
        if (other == com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse.getDefaultInstance()) return this;
        if (other.hasErrorCode()) {
          setErrorCode(other.getErrorCode());
        }
        if (other.hasRoleData()) {
          mergeRoleData(other.getRoleData());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setErrorCode(input.readInt32());
              break;
            }
            case 18: {
              com.randioo.mahjong_public_server.protocol.Entity.RoleData.Builder subBuilder = com.randioo.mahjong_public_server.protocol.Entity.RoleData.newBuilder();
              if (hasRoleData()) {
                subBuilder.mergeFrom(getRoleData());
              }
              input.readMessage(subBuilder, extensionRegistry);
              setRoleData(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // optional int32 errorCode = 1 [default = 1];
      public boolean hasErrorCode() {
        return result.hasErrorCode();
      }
      public int getErrorCode() {
        return result.getErrorCode();
      }
      public Builder setErrorCode(int value) {
        result.hasErrorCode = true;
        result.errorCode_ = value;
        return this;
      }
      public Builder clearErrorCode() {
        result.hasErrorCode = false;
        result.errorCode_ = 1;
        return this;
      }
      
      // optional .com.randioo.mahjong_public_server.protocol.RoleData roleData = 2;
      public boolean hasRoleData() {
        return result.hasRoleData();
      }
      public com.randioo.mahjong_public_server.protocol.Entity.RoleData getRoleData() {
        return result.getRoleData();
      }
      public Builder setRoleData(com.randioo.mahjong_public_server.protocol.Entity.RoleData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.hasRoleData = true;
        result.roleData_ = value;
        return this;
      }
      public Builder setRoleData(com.randioo.mahjong_public_server.protocol.Entity.RoleData.Builder builderForValue) {
        result.hasRoleData = true;
        result.roleData_ = builderForValue.build();
        return this;
      }
      public Builder mergeRoleData(com.randioo.mahjong_public_server.protocol.Entity.RoleData value) {
        if (result.hasRoleData() &&
            result.roleData_ != com.randioo.mahjong_public_server.protocol.Entity.RoleData.getDefaultInstance()) {
          result.roleData_ =
            com.randioo.mahjong_public_server.protocol.Entity.RoleData.newBuilder(result.roleData_).mergeFrom(value).buildPartial();
        } else {
          result.roleData_ = value;
        }
        result.hasRoleData = true;
        return this;
      }
      public Builder clearRoleData() {
        result.hasRoleData = false;
        result.roleData_ = com.randioo.mahjong_public_server.protocol.Entity.RoleData.getDefaultInstance();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:com.randioo.mahjong_public_server.protocol.MoneyExchangeResponse)
    }
    
    static {
      defaultInstance = new MoneyExchangeResponse(true);
      com.randioo.mahjong_public_server.protocol.MoneyExchange.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.randioo.mahjong_public_server.protocol.MoneyExchangeResponse)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeRequest_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeRequest_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeResponse_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeResponse_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023MoneyExchange.proto\022*com.randioo.mahjo" +
      "ng_public_server.protocol\032\014Entity.proto\"" +
      "0\n\024MoneyExchangeRequest\022\013\n\003num\030\001 \001(\005\022\013\n\003" +
      "add\030\002 \001(\010\"u\n\025MoneyExchangeResponse\022\024\n\ter" +
      "rorCode\030\001 \001(\005:\0011\022F\n\010roleData\030\002 \001(\01324.com" +
      ".randioo.mahjong_public_server.protocol." +
      "RoleData"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeRequest_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeRequest_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeRequest_descriptor,
              new java.lang.String[] { "Num", "Add", },
              com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest.class,
              com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest.Builder.class);
          internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeResponse_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeResponse_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_randioo_mahjong_public_server_protocol_MoneyExchangeResponse_descriptor,
              new java.lang.String[] { "ErrorCode", "RoleData", },
              com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse.class,
              com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.randioo.mahjong_public_server.protocol.Entity.getDescriptor(),
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
