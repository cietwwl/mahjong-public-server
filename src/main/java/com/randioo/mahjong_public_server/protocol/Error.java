// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Error.proto

package com.randioo.mahjong_public_server.protocol;

public final class Error {
  private Error() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public enum ErrorCode
      implements com.google.protobuf.ProtocolMessageEnum {
    OK(0, 1),
    NO_STRING(1, 2),
    NO_ROLE_ACCOUNT(2, 3),
    EXIST_ROLE(3, 4),
    CREATE_FAILED(4, 5),
    REJECT_LOGIN(5, 6),
    ACCOUNT_ILLEGEL(6, 7),
    IN_LOGIN(7, 8),
    NAME_SENSITIVE(8, 9),
    NO_ROLE_DATA(9, 10),
    NAME_REPEATED(10, 11),
    NAME_TOO_LONG(11, 12),
    NAME_SPECIAL_CHAR(12, 13),
    GAME_CREATE_ERROR(13, 14),
    GAME_JOIN_ERROR(14, 15),
    ROUND_ERROR(15, 16),
    MONEY_NUM_ERROR(16, 17),
    MATCH_ERROR_LOCK(17, 18),
    MATCH_MAX_ROLE_COUNT(18, 19),
    GAME_NOT_EXIST(19, 20),
    GAME_EXITING(20, 21),
    APPLY_REJECT(21, 22),
    NOT_YOUR_TURN(22, 23),
    NOT_SAME_TYPE(23, 24),
    SMALLER(24, 25),
    NULL_REJECT(25, 26),
    NOT_LANDLORD(26, 27),
    MINGPAI_FORBIDDEN(27, 28),
    FIRST_ROUND(28, 29),
    ;
    
    
    public final int getNumber() { return value; }
    
    public static ErrorCode valueOf(int value) {
      switch (value) {
        case 1: return OK;
        case 2: return NO_STRING;
        case 3: return NO_ROLE_ACCOUNT;
        case 4: return EXIST_ROLE;
        case 5: return CREATE_FAILED;
        case 6: return REJECT_LOGIN;
        case 7: return ACCOUNT_ILLEGEL;
        case 8: return IN_LOGIN;
        case 9: return NAME_SENSITIVE;
        case 10: return NO_ROLE_DATA;
        case 11: return NAME_REPEATED;
        case 12: return NAME_TOO_LONG;
        case 13: return NAME_SPECIAL_CHAR;
        case 14: return GAME_CREATE_ERROR;
        case 15: return GAME_JOIN_ERROR;
        case 16: return ROUND_ERROR;
        case 17: return MONEY_NUM_ERROR;
        case 18: return MATCH_ERROR_LOCK;
        case 19: return MATCH_MAX_ROLE_COUNT;
        case 20: return GAME_NOT_EXIST;
        case 21: return GAME_EXITING;
        case 22: return APPLY_REJECT;
        case 23: return NOT_YOUR_TURN;
        case 24: return NOT_SAME_TYPE;
        case 25: return SMALLER;
        case 26: return NULL_REJECT;
        case 27: return NOT_LANDLORD;
        case 28: return MINGPAI_FORBIDDEN;
        case 29: return FIRST_ROUND;
        default: return null;
      }
    }
    
    public static com.google.protobuf.Internal.EnumLiteMap<ErrorCode>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<ErrorCode>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ErrorCode>() {
            public ErrorCode findValueByNumber(int number) {
              return ErrorCode.valueOf(number)
    ;        }
          };
    
    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.randioo.mahjong_public_server.protocol.Error.getDescriptor().getEnumTypes().get(0);
    }
    
    private static final ErrorCode[] VALUES = {
      OK, NO_STRING, NO_ROLE_ACCOUNT, EXIST_ROLE, CREATE_FAILED, REJECT_LOGIN, ACCOUNT_ILLEGEL, IN_LOGIN, NAME_SENSITIVE, NO_ROLE_DATA, NAME_REPEATED, NAME_TOO_LONG, NAME_SPECIAL_CHAR, GAME_CREATE_ERROR, GAME_JOIN_ERROR, ROUND_ERROR, MONEY_NUM_ERROR, MATCH_ERROR_LOCK, MATCH_MAX_ROLE_COUNT, GAME_NOT_EXIST, GAME_EXITING, APPLY_REJECT, NOT_YOUR_TURN, NOT_SAME_TYPE, SMALLER, NULL_REJECT, NOT_LANDLORD, MINGPAI_FORBIDDEN, FIRST_ROUND, 
    };
    public static ErrorCode valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }
    private final int index;
    private final int value;
    private ErrorCode(int index, int value) {
      this.index = index;
      this.value = value;
    }
    
    static {
      com.randioo.mahjong_public_server.protocol.Error.getDescriptor();
    }
    
    // @@protoc_insertion_point(enum_scope:com.randioo.mahjong_public_server.protocol.ErrorCode)
  }
  
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\013Error.proto\022*com.randioo.mahjong_publi" +
      "c_server.protocol*\252\004\n\tErrorCode\022\006\n\002OK\020\001\022" +
      "\r\n\tNO_STRING\020\002\022\023\n\017NO_ROLE_ACCOUNT\020\003\022\016\n\nE" +
      "XIST_ROLE\020\004\022\021\n\rCREATE_FAILED\020\005\022\020\n\014REJECT" +
      "_LOGIN\020\006\022\023\n\017ACCOUNT_ILLEGEL\020\007\022\014\n\010IN_LOGI" +
      "N\020\010\022\022\n\016NAME_SENSITIVE\020\t\022\020\n\014NO_ROLE_DATA\020" +
      "\n\022\021\n\rNAME_REPEATED\020\013\022\021\n\rNAME_TOO_LONG\020\014\022" +
      "\025\n\021NAME_SPECIAL_CHAR\020\r\022\025\n\021GAME_CREATE_ER" +
      "ROR\020\016\022\023\n\017GAME_JOIN_ERROR\020\017\022\017\n\013ROUND_ERRO" +
      "R\020\020\022\023\n\017MONEY_NUM_ERROR\020\021\022\024\n\020MATCH_ERROR_",
      "LOCK\020\022\022\030\n\024MATCH_MAX_ROLE_COUNT\020\023\022\022\n\016GAME" +
      "_NOT_EXIST\020\024\022\020\n\014GAME_EXITING\020\025\022\020\n\014APPLY_" +
      "REJECT\020\026\022\021\n\rNOT_YOUR_TURN\020\027\022\021\n\rNOT_SAME_" +
      "TYPE\020\030\022\013\n\007SMALLER\020\031\022\017\n\013NULL_REJECT\020\032\022\020\n\014" +
      "NOT_LANDLORD\020\033\022\025\n\021MINGPAI_FORBIDDEN\020\034\022\017\n" +
      "\013FIRST_ROUND\020\035"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
