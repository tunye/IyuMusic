package com.iyuba.music.entity.user;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.iyuba.music.R;
import com.iyuba.music.util.ParameterUrl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 10202 on 2016/3/30.
 */
public class MostDetailInfo {
    @JSONField(name = "realname")
    public String realname;// 真实姓名
    @JSONField(name = "gender")
    public String gender;// 性别
    @JSONField(name = "birthday")
    public String birthday;// 生日
    @JSONField(name = "constellation")
    public String constellation;// 星座
    @JSONField(name = "zodiac")
    public String zodiac;// 生肖
    @JSONField(name = "telephone")
    public String telephone;// 联系电话
    @JSONField(name = "mobile")
    public String mobile;// 手机
    @JSONField(name = "idcardtype")
    public String idcardtype;// 证件类型
    @JSONField(name = "idcard")
    public String idcard;// 证件号
    @JSONField(name = "address")
    public String address;// 邮件地址
    @JSONField(name = "zipcode")
    public String zipcode;// 邮编
    @JSONField(name = "nationality")
    public String nationality;// 国籍
    @JSONField(name = "birthLocation")
    public String birthLocation;// 出生地
    @JSONField(name = "resideLocation")
    public String resideLocation;// 现住地
    @JSONField(name = "graduateschool")
    public String graduateschool;// 毕业学校
    @JSONField(name = "company")
    public String company;// 公司
    @JSONField(name = "education")
    public String education;// 学历
    @JSONField(name = "occupation")
    public String occupation;// 职业
    @JSONField(name = "position")
    public String position;// 职位
    @JSONField(name = "revenue")
    public String revenue;// 年收入
    @JSONField(name = "affectivestatus")
    public String affectivestatus;// 情感状态
    @JSONField(name = "lookingfor")
    public String lookingfor;// 交友目的
    @JSONField(name = "bloodtype")
    public String bloodtype;// 血型
    @JSONField(name = "height")
    public String height;// 身高
    @JSONField(name = "weight")
    public String weight;// 体重
    @JSONField(name = "bio")
    public String bio;// 自我介绍
    @JSONField(name = "interest")
    public String interest;// 兴趣爱好

    public void format(Context context, MostDetailInfo mostDetailInfo) {
        Class c = MostDetailInfo.class;
        //获取所有属性
        Field[] fs = c.getDeclaredFields();
        Method getMethod, setMethod;
        Object value;
        String property;
        String wantValue;
        for (Field field : fs) {
            property = ParameterUrl.captureName(field.getName());
            try {
                getMethod = c.getMethod("get" + property);
                value = getMethod.invoke(mostDetailInfo);
                if (value instanceof String) {
                    wantValue = (value.toString()).trim();
                    if (TextUtils.isEmpty(wantValue)) {
                        wantValue = context.getString(R.string.person_detail_undefined);
                    }
                    setMethod = c.getMethod("set" + property, String.class);
                    setMethod.invoke(mostDetailInfo, wantValue);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getZodiac() {
        return zodiac;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdcardtype() {
        return idcardtype;
    }

    public void setIdcardtype(String idcardtype) {
        this.idcardtype = idcardtype;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthLocation() {
        return birthLocation;
    }

    public void setBirthLocation(String birthLocation) {
        this.birthLocation = birthLocation;
    }

    public String getResideLocation() {
        return resideLocation;
    }

    public void setResideLocation(String resideLocation) {
        this.resideLocation = resideLocation;
    }

    public String getGraduateschool() {
        return graduateschool;
    }

    public void setGraduateschool(String graduateschool) {
        this.graduateschool = graduateschool;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getAffectivestatus() {
        return affectivestatus;
    }

    public void setAffectivestatus(String affectivestatus) {
        this.affectivestatus = affectivestatus;
    }

    public String getLookingfor() {
        return lookingfor;
    }

    public void setLookingfor(String lookingfor) {
        this.lookingfor = lookingfor;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
