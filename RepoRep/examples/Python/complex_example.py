import random
import json
import os
import logging
from datetime import datetime
from flask import Flask, jsonify, request
from sqlalchemy import create_engine, Column, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import requests
import pandas as pd
import matplotlib.pyplot as plt

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# 创建Flask应用
app = Flask(__name__)

# 配置数据库
Base = declarative_base()
engine = create_engine('sqlite:///users.db')
Session = sessionmaker(bind=engine)

class User(Base):
    __tablename__ = 'users'
    id = Column(Integer, primary_key=True)
    name = Column(String)
    age = Column(Integer)
    email = Column(String)

Base.metadata.create_all(engine)

def generate_random_user():
    names = ["张三", "李四", "王五", "赵六", "钱七"]
    return {
        "name": random.choice(names),
        "age": random.randint(18, 60),
        "email": f"{random.choice(names)}@example.com"
    }

def save_user_to_db(user_data):
    session = Session()
    new_user = User(**user_data)
    session.add(new_user)
    session.commit()
    session.close()
    logger.info(f"用户已保存到数据库: {user_data}")

@app.route('/user', methods=['POST'])
def create_user():
    user_data = generate_random_user()
    save_user_to_db(user_data)
    return jsonify(user_data), 201

@app.route('/users', methods=['GET'])
def get_users():
    session = Session()
    users = session.query(User).all()
    user_list = [{"id": user.id, "name": user.name, "age": user.age, "email": user.email} for user in users]
    session.close()
    return jsonify(user_list)

def fetch_weather_data(city):
    api_key = "your_api_key_here"  # 请替换为您的实际API密钥
    url = f"http://api.openweathermap.org/data/2.5/weather?q={city}&appid={api_key}&units=metric"
    response = requests.get(url)
    return response.json()

def analyze_user_data():
    session = Session()
    users = session.query(User).all()
    ages = [user.age for user in users]
    
    plt.figure(figsize=(10, 6))
    plt.hist(ages, bins=10, edgecolor='black')
    plt.title('用户年龄分布')
    plt.xlabel('年龄')
    plt.ylabel('用户数量')
    plt.savefig('user_age_distribution.png')
    plt.close()
    
    df = pd.DataFrame([(user.name, user.age, user.email) for user in users], columns=['姓名', '年龄', '邮箱'])
    df.to_csv('user_data.csv', index=False, encoding='utf-8-sig')
    session.close()
    logger.info("用户数据分析完成，图表和CSV文件已生成")

@app.route('/analyze', methods=['GET'])
def run_analysis():
    analyze_user_data()
    return jsonify({"message": "分析完成，请查看生成的文件"}), 200

if __name__ == '__main__':
    logger.info("应用程序启动")
    app.run(debug=True)
