package com.abolent.skillSharev2.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String senderEmail;
    private String receiverEmail;
    private String content;


}
