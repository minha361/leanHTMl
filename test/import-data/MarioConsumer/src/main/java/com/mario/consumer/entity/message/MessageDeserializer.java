package com.mario.consumer.entity.message;

public interface MessageDeserializer {

	Message deserialize(Object data) throws Exception;
}
