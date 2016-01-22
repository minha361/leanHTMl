/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucidworks.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ndn
 */
public class ACSIIFolding {

    private final Map<String, String> map1 = new HashMap<>();
    private final Map<String, String> map2 = new HashMap<>();
    private final Map<String, String> map3 = new HashMap<>();
    private final Set<String> special = new HashSet<>();

    public ACSIIFolding() {
        map1.put("Ạ", "Ạ");
        map1.put("˜a", "ã");
        map1.put("u˜", "ũ");
        map1.put("˜e", "ẽ");
        map1.put("˜i", "ĩ");
        map1.put("˜o", "õ");
        map1.put("e˜", "ẽ");
        map1.put("˜u", "ũ");
        map1.put("̆a", "ă");
        map1.put("˜y", "ỹ");
        map1.put("U˜", "Ũ");
        map1.put("ù", "ù");
        map1.put("ú", "ú");
        map1.put("ũ", "ũ");
        map1.put("ủ", "ủ");
        map1.put("E˜", "Ẽ");
        map1.put("è", "è");
        map1.put("é", "é");
        map1.put("ê", "ê");
        map1.put("ẽ", "ẽ");
        map1.put("̀A", "À");
        map1.put("ẻ", "ẻ");
        map1.put("̀E", "È");
        map1.put("ư", "ư");
        map1.put("̀I", "Ì");
        map1.put("Ù", "Ù");
        map1.put("Ú", "Ú");
        map1.put("o˜", "õ");
        map1.put("Ũ", "Ũ");
        map1.put("ụ", "ụ");
        map1.put("̀O", "Ò");
        map1.put("Ủ", "Ủ");
        map1.put("̀U", "Ù");
        map1.put("̉A", "Ả");
        map1.put("̀Y", "Ỳ");
        map1.put("È", "È");
        map1.put("É", "É");
        map1.put("̉E", "Ẻ");
        map1.put("Ê", "Ê");
        map1.put("Ẽ", "Ẽ");
        map1.put("ẹ", "ẹ");
        map1.put("́A", "Á");
        map1.put("̉I", "Ỉ");
        map1.put("̀a", "à");
        map1.put("́E", "É");
        map1.put("Ẻ", "Ẻ");
        map1.put("̀e", "è");
        map1.put("̉O", "Ỏ");
        map1.put("Ư", "Ư");
        map1.put("́I", "Í");
        map1.put("̀i", "ì");
        map1.put("̉U", "Ủ");
        map1.put("O˜", "Õ");
        map1.put("́O", "Ó");
        map1.put("Ụ", "Ụ");
        map1.put("̀o", "ò");
        map1.put("̉Y", "Ỷ");
        map1.put("ò", "ò");
        map1.put("ó", "ó");
        map1.put("ô", "ô");
        map1.put("õ", "õ");
        map1.put("́U", "Ú");
        map1.put("̀u", "ù");
        map1.put("̉a", "ả");
        map1.put("́Y", "Ý");
        map1.put("̀y", "ỳ");
        map1.put("ỏ", "ỏ");
        map1.put("̉e", "ẻ");
        map1.put("̣A", "Ạ");
        map1.put("Ẹ", "Ẹ");
        map1.put("̂A", "Â");
        map1.put("́a", "á");
        map1.put("̉i", "ỉ");
        map1.put("̣E", "Ẹ");
        map1.put("̂E", "Ê");
        map1.put("y˜", "ỹ");
        map1.put("́e", "é");
        map1.put("̣I", "Ị");
        map1.put("̉o", "ỏ");
        map1.put("́i", "í");
        map1.put("̣O", "Ọ");
        map1.put("ơ", "ơ");
        map1.put("̉u", "ủ");
        map1.put("̂O", "Ô");
        map1.put("́o", "ó");
        map1.put("̉y", "ỷ");
        map1.put("Ò", "Ò");
        map1.put("Ó", "Ó");
        map1.put("̣U", "Ụ");
        map1.put("i˜", "ĩ");
        map1.put("Ô", "Ô");
        map1.put("Õ", "Õ");
        map1.put("ọ", "ọ");
        map1.put("̛O", "Ơ");
        map1.put("́u", "ú");
        map1.put("̣Y", "Ỵ");
        map1.put("́y", "ý");
        map1.put("Ỏ", "Ỏ");
        map1.put("̛U", "Ư");
        map1.put("a˜", "ã");
        map1.put("̃A", "Ã");
        map1.put("̣a", "ạ");
        map1.put("̂a", "â");
        map1.put("̃E", "Ẽ");
        map1.put("̣e", "ẹ");
        map1.put("̂e", "ê");
        map1.put("Y˜", "Ỹ");
        map1.put("̃I", "Ĩ");
        map1.put("̣i", "ị");
        map1.put("ỳ", "ỳ");
        map1.put("ý", "ý");
        map1.put("ỹ", "ỹ");
        map1.put("̃O", "Õ");
        map1.put("̣o", "ọ");
        map1.put("Ơ", "Ơ");
        map1.put("̂o", "ô");
        map1.put("ỷ", "ỷ");
        map1.put("̃U", "Ũ");
        map1.put("̣u", "ụ");
        map1.put("I˜", "Ĩ");
        map1.put("Ọ", "Ọ");
        map1.put("̛o", "ơ");
        map1.put("̃Y", "Ỹ");
        map1.put("̣y", "ỵ");
        map1.put("ì", "ì");
        map1.put("í", "í");
        map1.put("ĩ", "ĩ");
        map1.put("̛u", "ư");
        map1.put("A˜", "Ã");
        map1.put("̃a", "ã");
        map1.put("à", "à");
        map1.put("á", "á");
        map1.put("ỉ", "ỉ");
        map1.put("â", "â");
        map1.put("ã", "ã");
        map1.put("̃e", "ẽ");
        map1.put("ă", "ă");
        map1.put("̃i", "ĩ");
        map1.put("Ỳ", "Ỳ");
        map1.put("ả", "ả");
        map1.put("Ý", "Ý");
        map1.put("Ỹ", "Ỹ");
        map1.put("ỵ", "ỵ");
        map1.put("̃o", "õ");
        map1.put("Ỷ", "Ỷ");
        map1.put("̃u", "ũ");
        map1.put("̃y", "ỹ");
        map1.put("Ì", "Ì");
        map1.put("Í", "Í");
        map1.put("Ĩ", "Ĩ");
        map1.put("ị", "ị");
        map1.put("À", "À");
        map1.put("Á", "Á");
        map1.put("Ỉ", "Ỉ");
        map1.put("Â", "Â");
        map1.put("Ã", "Ã");
        map1.put("ạ", "ạ");
        map1.put("˜A", "Ã");
        map1.put("Ă", "Ă");
        map1.put("Ả", "Ả");
        map1.put("˜E", "Ẽ");
        map1.put("Ỵ", "Ỵ");
        map1.put("˜I", "Ĩ");
        map1.put("˜O", "Õ");
        map1.put("˜U", "Ũ");
        map1.put("Ị", "Ị");
        map1.put("̆A", "Ă");
        map1.put("˜Y", "Ỹ");

        map2.put("̂Ã", "Ẫ");
        map2.put("́â", "ấ");
        map2.put("̀Ă", "Ằ");
        map2.put("̀ă", "ằ");
        map2.put("̂È", "Ề");
        map2.put("̂É", "Ế");
        map2.put("̣Ê", "Ệ");
        map2.put("́ê", "ế");
        map2.put("̂Ọ", "Ộ");
        map2.put("̂ọ", "ộ");
        map2.put("̂Ỏ", "Ổ");
        map2.put("̂ỏ", "ổ");
        map2.put("Ở", "Ở");
        map2.put("̂Ò", "Ồ");
        map2.put("ỏ̂", "ổ");
        map2.put("̂Ó", "Ố");
        map2.put("̣Ô", "Ộ");
        map2.put("̂Õ", "Ỗ");
        map2.put("́ô", "ố");
        map2.put("Ă˜", "Ẵ");
        map2.put("̂à", "ầ");
        map2.put("̂á", "ấ");
        map2.put("̃Â", "Ẫ");
        map2.put("̣â", "ậ");
        map2.put("̂ã", "ẫ");
        map2.put("́Ă", "Ắ");
        map2.put("à̂", "ầ");
        map2.put("́ă", "ắ");
        map2.put("à̆", "ằ");
        map2.put("̂è", "ề");
        map2.put("̂é", "ế");
        map2.put("̃Ê", "Ễ");
        map2.put("̣ê", "ệ");
        map2.put("ở", "ở");
        map2.put("̂ò", "ồ");
        map2.put("̂ó", "ố");
        map2.put("̃Ô", "Ỗ");
        map2.put("̣ô", "ộ");
        map2.put("̂õ", "ỗ");
        map2.put("ă˜", "ẵ");
        map2.put("â˜", "ẫ");
        map2.put("Ằ", "Ằ");
        map2.put("̃â", "ẫ");
        map2.put("Ắ", "Ắ");
        map2.put("̣Ă", "Ặ");
        map2.put("̣ă", "ặ");
        map2.put("á̂", "ấ");
        map2.put("Ẵ", "Ẵ");
        map2.put("À̂", "Ầ");
        map2.put("˜Ơ", "Ỡ");
        map2.put("á̆", "ắ");
        map2.put("˜ơ", "ỡ");
        map2.put("À̆", "Ằ");
        map2.put("Ẳ", "Ẳ");
        map2.put("̃ê", "ễ");
        map2.put("ò̂", "ồ");
        map2.put("̃ô", "ỗ");
        map2.put("˜Ư", "Ữ");
        map2.put("˜ư", "ữ");
        map2.put("̆Ạ", "Ặ");
        map2.put("Â˜", "Ẫ");
        map2.put("̆ạ", "ặ");
        map2.put("̆Ả", "Ẳ");
        map2.put("̆ả", "ẳ");
        map2.put("ằ", "ằ");
        map2.put("ầ", "ầ");
        map2.put("ắ", "ắ");
        map2.put("ấ", "ấ");
        map2.put("̃Ă", "Ẵ");
        map2.put("ẵ", "ẵ");
        map2.put("̃ă", "ẵ");
        map2.put("Á̂", "Ấ");
        map2.put("Ậ", "Ậ");
        map2.put("ẫ", "ẫ");
        map2.put("Ặ", "Ặ");
        map2.put("Á̆", "Ắ");
        map2.put("Ặ", "Ặ");
        map2.put("ẳ", "ẳ");
        map2.put("ẩ", "ẩ");
        map2.put("ô˜", "ỗ");
        map2.put("ờ", "ờ");
        map2.put("ó̂", "ố");
        map2.put("Ò̂", "Ồ");
        map2.put("̆À", "Ằ");
        map2.put("̆Á", "Ắ");
        map2.put("̆Ã", "Ẵ");
        map2.put("Ầ", "Ầ");
        map2.put("ã̂", "ẫ");
        map2.put("Ấ", "Ấ");
        map2.put("ặ", "ặ");
        map2.put("Ẫ", "Ẫ");
        map2.put("ậ", "ậ");
        map2.put("ậ", "ậ");
        map2.put("ã̆", "ẵ");
        map2.put("ặ", "ặ");
        map2.put("Ẩ", "Ẩ");
        map2.put("ớ", "ớ");
        map2.put("Ô˜", "Ỗ");
        map2.put("Ờ", "Ờ");
        map2.put("ồ", "ồ");
        map2.put("ố", "ố");
        map2.put("Ó̂", "Ố");
        map2.put("ỗ", "ỗ");
        map2.put("ổ", "ổ");
        map2.put("̆à", "ằ");
        map2.put("̆á", "ắ");
        map2.put("̆ã", "ẵ");
        map2.put("Ã̂", "Ẫ");
        map2.put("̀Ơ", "Ờ");
        map2.put("Ậ", "Ậ");
        map2.put("̀ơ", "ờ");
        map2.put("Ã̆", "Ẵ");
        map2.put("Ả̂", "Ẩ");
        map2.put("Ả̆", "Ẳ");
        map2.put("Ớ", "Ớ");
        map2.put("Ồ", "Ồ");
        map2.put("õ̂", "ỗ");
        map2.put("Ố", "Ố");
        map2.put("Ỗ", "Ỗ");
        map2.put("ộ", "ộ");
        map2.put("̀Ư", "Ừ");
        map2.put("̀ư", "ừ");
        map2.put("Ữ", "Ữ");
        map2.put("Ự", "Ự");
        map2.put("Ổ", "Ổ");
        map2.put("ả̂", "ẩ");
        map2.put("́Ơ", "Ớ");
        map2.put("ả̆", "ẳ");
        map2.put("́ơ", "ớ");
        map2.put("ỡ", "ỡ");
        map2.put("Õ̂", "Ỗ");
        map2.put("́Ư", "Ứ");
        map2.put("Ộ", "Ộ");
        map2.put("́ư", "ứ");
        map2.put("ữ", "ữ");
        map2.put("ự", "ự");
        map2.put("̉Â", "Ẩ");
        map2.put("̣Ơ", "Ợ");
        map2.put("̣ơ", "ợ");
        map2.put("̉Ê", "Ể");
        map2.put("Ỡ", "Ỡ");
        map2.put("̉Ô", "Ổ");
        map2.put("̣Ư", "Ự");
        map2.put("̣ư", "ự");
        map2.put("Ử", "Ử");
        map2.put("̉â", "ẩ");
        map2.put("̃Ơ", "Ỡ");
        map2.put("̃ơ", "ỡ");
        map2.put("̉ê", "ể");
        map2.put("̉ô", "ổ");
        map2.put("̃Ư", "Ữ");
        map2.put("Ư˜", "Ữ");
        map2.put("̃ư", "ữ");
        map2.put("̛Ọ", "Ợ");
        map2.put("̛ọ", "ợ");
        map2.put("̛Ỏ", "Ở");
        map2.put("̛ỏ", "ở");
        map2.put("̛Ò", "Ờ");
        map2.put("ử", "ử");
        map2.put("̛Ó", "Ớ");
        map2.put("̉Ă", "Ẳ");
        map2.put("̉ă", "ẳ");
        map2.put("è̂", "ề");
        map2.put("̛Õ", "Ỡ");
        map2.put("̛Ù", "Ừ");
        map2.put("̛Ú", "Ứ");
        map2.put("̛Ụ", "Ự");
        map2.put("̛ụ", "ự");
        map2.put("̛Ủ", "Ử");
        map2.put("̛ủ", "ử");
        map2.put("ư˜", "ữ");
        map2.put("Ừ", "Ừ");
        map2.put("ê˜", "ễ");
        map2.put("Ứ", "Ứ");
        map2.put("Ữ", "Ữ");
        map2.put("̛ò", "ờ");
        map2.put("̛ó", "ớ");
        map2.put("é̂", "ế");
        map2.put("È̂", "Ề");
        map2.put("̛õ", "ỡ");
        map2.put("Ử", "Ử");
        map2.put("Ơ˜", "Ỡ");
        map2.put("̛ù", "ừ");
        map2.put("̛ú", "ứ");
        map2.put("ừ", "ừ");
        map2.put("Ệ", "Ệ");
        map2.put("ừ", "ừ");
        map2.put("ứ", "ứ");
        map2.put("Ê˜", "Ễ");
        map2.put("ữ", "ữ");
        map2.put("Ự", "Ự");
        map2.put("ề", "ề");
        map2.put("ế", "ế");
        map2.put("É̂", "Ế");
        map2.put("ễ", "ễ");
        map2.put("ử", "ử");
        map2.put("ơ˜", "ỡ");
        map2.put("ể", "ể");
        map2.put("Ờ", "Ờ");
        map2.put("Ớ", "Ớ");
        map2.put("ứ", "ứ");
        map2.put("Ừ", "Ừ");
        map2.put("Ỡ", "Ỡ");
        map2.put("˜Â", "Ẫ");
        map2.put("Ở", "Ở");
        map2.put("ệ", "ệ");
        map2.put("˜Ê", "Ễ");
        map2.put("ự", "ự");
        map2.put("Ề", "Ề");
        map2.put("Ế", "Ế");
        map2.put("˜Ô", "Ỗ");
        map2.put("Ễ", "Ễ");
        map2.put("ệ", "ệ");
        map2.put("Ể", "Ể");
        map2.put("ờ", "ờ");
        map2.put("ớ", "ớ");
        map2.put("Ứ", "Ứ");
        map2.put("ỡ", "ỡ");
        map2.put("Ợ", "Ợ");
        map2.put("˜â", "ẫ");
        map2.put("ở", "ở");
        map2.put("Ẻ̂", "Ể");
        map2.put("˜ê", "ễ");
        map2.put("˜ô", "ỗ");
        map2.put("Ệ", "Ệ");
        map2.put("ợ", "ợ");
        map2.put("ẻ̂", "ể");
        map2.put("˜Ă", "Ẵ");
        map2.put("˜ă", "ẵ");
        map2.put("̛Ũ", "Ữ");
        map2.put("̛ũ", "ữ");
        map2.put("Ộ", "Ộ");
        map2.put("̉Ơ", "Ở");
        map2.put("̉ơ", "ở");
        map2.put("̀Â", "Ầ");
        map2.put("Ẽ̂", "Ễ");
        map2.put("̉Ư", "Ử");
        map2.put("̉ư", "ử");
        map2.put("̀Ê", "Ề");
        map2.put("Ợ", "Ợ");
        map2.put("̀Ô", "Ồ");
        map2.put("ộ", "ộ");
        map2.put("̂Ạ", "Ậ");
        map2.put("̂ạ", "ậ");
        map2.put("̂Ả", "Ẩ");
        map2.put("̂ả", "ẩ");
        map2.put("́Â", "Ấ");
        map2.put("̀â", "ầ");
        map2.put("ẽ̂", "ễ");
        map2.put("́Ê", "Ế");
        map2.put("̀ê", "ề");
        map2.put("ợ", "ợ");
        map2.put("́Ô", "Ố");
        map2.put("̀ô", "ồ");
        map2.put("̂Ẹ", "Ệ");
        map2.put("̂ẹ", "ệ");
        map2.put("Ỏ̂", "Ổ");
        map2.put("̂Ẻ", "Ể");
        map2.put("̂ẻ", "ể");
        map2.put("̂Ẽ", "Ễ");
        map2.put("̂ẽ", "ễ");
        map2.put("̂À", "Ầ");
        map2.put("̂Á", "Ấ");
        map2.put("̣Â", "Ậ");

        special.add("˜");
        special.add("̀");
        special.add("́");
        special.add("̃ ");
        special.add("̉");
        special.add("̣");
        special.add("̂");
        special.add("̆");
        special.add("̛");

        map3.put("Ă", "a");
        map3.put("ă", "a");
        map3.put("Ĩ", "i");
        map3.put("ĩ", "i");
        map3.put("Ũ", "u");
        map3.put("ũ", "u");
        map3.put("Ạ", "a");
        map3.put("Ơ", "o");
        map3.put("ơ", "o");
        map3.put("ạ", "a");
        map3.put("Ả", "a");
        map3.put("ả", "a");
        map3.put("Ấ", "a");
        map3.put("ấ", "a");
        map3.put("Ầ", "a");
        map3.put("ầ", "a");
        map3.put("Ẩ", "a");
        map3.put("ẩ", "a");
        map3.put("Ẫ", "a");
        map3.put("ẫ", "a");
        map3.put("Ậ", "a");
        map3.put("ậ", "a");
        map3.put("Ắ", "a");
        map3.put("Ư", "u");
        map3.put("ắ", "a");
        map3.put("ư", "u");
        map3.put("Ằ", "a");
        map3.put("ằ", "a");
        map3.put("Ẳ", "a");
        map3.put("ẳ", "a");
        map3.put("Ẵ", "a");
        map3.put("ẵ", "a");
        map3.put("Ặ", "a");
        map3.put("ặ", "a");
        map3.put("Ẹ", "e");
        map3.put("ẹ", "e");
        map3.put("Ẻ", "e");
        map3.put("ẻ", "e");
        map3.put("Ẽ", "e");
        map3.put("ẽ", "e");
        map3.put("Ế", "e");
        map3.put("ế", "e");
        map3.put("À", "a");
        map3.put("Ề", "e");
        map3.put("Á", "a");
        map3.put("ề", "e");
        map3.put("Â", "a");
        map3.put("Ể", "e");
        map3.put("Ã", "a");
        map3.put("ể", "e");
        map3.put("Ễ", "e");
        map3.put("ễ", "e");
        map3.put("Ệ", "e");
        map3.put("ệ", "e");
        map3.put("È", "e");
        map3.put("Ỉ", "i");
        map3.put("É", "e");
        map3.put("ỉ", "i");
        map3.put("Ê", "e");
        map3.put("Ị", "i");
        map3.put("ị", "i");
        map3.put("Ì", "i");
        map3.put("Ọ", "o");
        map3.put("Í", "i");
        map3.put("ọ", "o");
        map3.put("Ỏ", "o");
        map3.put("ỏ", "o");
        map3.put("Ố", "o");
        map3.put("ố", "o");
        map3.put("Ò", "o");
        map3.put("Ồ", "o");
        map3.put("Ó", "o");
        map3.put("ồ", "o");
        map3.put("Ô", "o");
        map3.put("Ổ", "o");
        map3.put("Õ", "o");
        map3.put("ổ", "o");
        map3.put("Ỗ", "o");
        map3.put("ỗ", "o");
        map3.put("Ộ", "o");
        map3.put("Ù", "u");
        map3.put("ộ", "o");
        map3.put("Ú", "u");
        map3.put("Ớ", "o");
        map3.put("ớ", "o");
        map3.put("Ờ", "o");
        map3.put("Ý", "y");
        map3.put("ờ", "o");
        map3.put("Ở", "o");
        map3.put("ở", "o");
        map3.put("à", "a");
        map3.put("Ỡ", "o");
        map3.put("á", "a");
        map3.put("ỡ", "o");
        map3.put("â", "a");
        map3.put("Ợ", "o");
        map3.put("ã", "a");
        map3.put("ợ", "o");
        map3.put("Ụ", "u");
        map3.put("ụ", "u");
        map3.put("Ủ", "u");
        map3.put("ủ", "u");
        map3.put("è", "e");
        map3.put("Ứ", "u");
        map3.put("é", "e");
        map3.put("ứ", "u");
        map3.put("ê", "e");
        map3.put("Ừ", "u");
        map3.put("ừ", "u");
        map3.put("ì", "i");
        map3.put("Ử", "u");
        map3.put("í", "i");
        map3.put("ử", "u");
        map3.put("Ữ", "u");
        map3.put("ữ", "u");
        map3.put("Ự", "u");
        map3.put("ự", "u");
        map3.put("Ỳ", "y");
        map3.put("ò", "o");
        map3.put("ó", "o");
        map3.put("ỳ", "y");
        map3.put("ô", "o");
        map3.put("Ỵ", "y");
        map3.put("õ", "o");
        map3.put("ỵ", "y");
        map3.put("Ỷ", "y");
        map3.put("ỷ", "y");
        map3.put("Ỹ", "y");
        map3.put("ỹ", "y");
        map3.put("ù", "u");
        map3.put("ú", "u");
        map3.put("ý", "y");
    }

    public String transformToUTF8(String input) {
        String output = input.trim().replaceAll("5˜10g", "5~10g");
        for (Map.Entry<String, String> e : map1.entrySet()) {
            if (output.contains(e.getKey())) {
                output = output.replaceAll(e.getKey(), e.getValue());
            }
            if (output.contains(e.getKey().toUpperCase())) {
                output = output.replaceAll(e.getKey().toUpperCase(), e.getValue().toUpperCase());
            }
        }
        for (Map.Entry<String, String> e : map2.entrySet()) {
            if (output.contains(e.getKey())) {
                output = output.replaceAll(e.getKey(), e.getValue());
            }
            if (output.contains(e.getKey().toUpperCase())) {
                output = output.replaceAll(e.getKey().toUpperCase(), e.getValue().toUpperCase());
            }
        }

        for (String key : special) {
            output = output.replaceAll(key, "");
        }
        return output;
    }

    public String acsiiFolding(String input) {
        String output = transformToUTF8(input);
        for (Map.Entry<String, String> e : map3.entrySet()) {
            if (output.contains(e.getKey())) {
                output = output.replaceAll(e.getKey(), e.getValue());
            }
            if (output.contains(e.getKey().toUpperCase())) {
                output = output.replaceAll(e.getKey().toUpperCase(), e.getValue().toUpperCase());
            }
        }

        return output;
    }
    
    public String acsiiFoldingWithoutTransformToUTF8(String input) {
        String output = input.trim().replaceAll("5˜10g", "5~10g");
        for (Map.Entry<String, String> e : map3.entrySet()) {
            if (output.contains(e.getKey())) {
                output = output.replaceAll(e.getKey(), e.getValue());
            }
            if (output.contains(e.getKey().toUpperCase())) {
                output = output.replaceAll(e.getKey().toUpperCase(), e.getValue().toUpperCase());
            }
        }

        return output;
    }
}
