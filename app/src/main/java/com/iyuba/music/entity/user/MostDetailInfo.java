package com.iyuba.music.entity.user;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyuba.music.R;
import com.iyuba.music.util.TextAttr;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 10202 on 2016/3/30.
 */
public class MostDetailInfo {
    @SerializedName("realname")
    public String realname;// 真实姓名
    @SerializedName("gender")
    public String gender;// 性别
    @SerializedName("birthday")
    public String birthday;// 生日
    @SerializedName("constellation")
    public String constellation;// 星座
    @SerializedName("zodiac")
    public String zodiac;// 生肖
    @SerializedName("telephone")
    public String telephone;// 联系电话
    @SerializedName("mobile")
    public String mobile;// 手机
    @SerializedName("idcardtype")
    public String idcardtype;// 证件类型
    @SerializedName("idcard")
    public String idcard;// 证件号
    @SerializedName("address")
    public String address;// 邮件地址
    @SerializedName("zipcode")
    public String zipcode;// 邮编
    @SerializedName("nationality")
    public String nationality;// 国籍
    @SerializedName("birthLocation")
    public String birthLocation;// 出生地
    @SerializedName("resideLocation")
    public String resideLocation;// 现住地
    @SerializedName("graduateschool")
    public String graduateschool;// 毕业学校
    @SerializedName("company")
    public String company;// 公司
    @SerializedName("education")
    public String education;// 学历
    @SerializedName("occupation")
    public String occupation;// 职业
    @SerializedName("position")
    public String position;// 职位
    @SerializedName("revenue")
    public String revenue;// 年收入
    @SerializedName("affectivestatus")
    public String affectivestatus;// 情感状态
    @SerializedName("lookingfor")
    public String lookingfor;// 交友目的
    @SerializedName("bloodtype")
    public String bloodtype;// 血型
    @SerializedName("height")
    public String height;// 身高
    @SerializedName("weight")
    public String weight;// 体重
    @SerializedName("bio")
    public String bio;// 自我介绍
    @SerializedName("interest")
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
            property = TextAttr.captureName(field.getName());
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
