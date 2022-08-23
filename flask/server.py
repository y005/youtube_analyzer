from flask import Flask, request
from keras.models import load_model
from konlpy.tag import Okt
import pandas as pd
import numpy as np

model = load_model('./movie_review_model.h5')
selected_tokens = pd.read_csv("./token.csv")
okt = Okt()

selected_words = []
for row in range(len(selected_tokens)):
    token = selected_tokens["토큰"][row]
    selected_words.append(token)

def extract_keyword(comments):
    keywords = okt.nouns(comments)
    main_keyword = extract_main_keyword(keywords)
    return ','.join(main_keyword)

def extract_main_keyword(keywords):
    dict = {}
    for keyword in keywords:
        if keyword in list(dict.keys()):
            dict[keyword] = 1 + dict[keyword]
        else:
            dict[keyword] = 1
    main_keyword = sorted(dict.items(), key = lambda item: item[1], reverse = True)
    result = []
    for keyword in main_keyword:
        value = list(keyword)
        result.append(value[0])

    #상위 10개 주요 키워드만 추출
    if len(result) < 20:
        return result
    else:
        return result[0:19]

def sentiment_analysis_comments(comments):
    count = 0
    comments = comments.split(',')
    for comment in comments:
        result = sentiment_analysis(comment)
        if result == "긍정":
            count += 1
    answer = float(count) * 100 / len(comments)
    return answer

def sentiment_analysis(comment):
    tokens = tokenize(comment)
    tfq = term_frequency(tokens)
    data = np.expand_dims(np.asarray(tfq).astype('float32'), axis=0)
    score = float(model.predict(data))
    if(score > 0.5):
        return "긍정"
    else:
        return "부정"

def tokenize(comment):
    answer = []
    tokens= okt.pos(comment)
    for tk in tokens:
        element = '/'.join(tk)
        answer.append(element)
    return answer

def term_frequency(tokens):#문장의 벡터화 함수
    answer = []
    for famous_token in selected_words:
        answer.append(tokens.count(famous_token))
    return answer

app = Flask(__name__)
@app.route('/analysis', methods = ['POST', 'GET'])
def result():
    if request.method == 'POST':
        body = request.get_json()
        comments = body["comments"]
        main_keywords = extract_keyword(comments)
        percent = sentiment_analysis_comments(comments)
        response = {
            'percent' : percent,
            'keywords' : main_keywords
        }
        return response

if __name__ == '__main__':
    app.run(host='0.0.0.0',port=5000)