from splinter import Browser
import pyrebase
import html2text

import firebase_admin

cred = firebase_admin.credentials.Certificate("leetcode-firebase-adminsdk.json")
firebase_admin.initialize_app(cred)

config = {
  "apiKey": "AIzaSyDvIRmgbuN-WXfQue51ykqmrBem8_IGEwM",
  "authDomain": "leetcode-d1d73.firebaseapp.com",
  "databaseURL": "https://leetcode-d1d73.firebaseio.com",
  "storageBucket": "gs://leetcode-d1d73.appspot.com"
}
firebase = pyrebase.initialize_app(config)
auth = firebase.auth()
user = auth.sign_in_with_email_and_password('richd.yang@gmail.com', '723neoidea')
db = firebase.database()


# h = html2text.HTML2Text()
# h.ignore_links = False

browser = Browser('chrome')

# login
# browser.visit('https://leetcode.com/accounts/login/')
#
# browser.fill('login', 'richd.yang@gmail.com')
# browser.fill('password', '723neoidea')
#
# browser.find_by_text('Sign In').first.click()

# go to algorithms
browser.visit('https://leetcode.com/problemset/algorithms')
browser.find_by_css('span.row-selector select option').last.click()

rows = browser.find_by_css('#question-app table tbody.reactable-data tr')
algorithms = []
for row in rows:
    cols = row.find_by_css('td')
    a = cols[2].find_by_css('a').first

    num = int(cols[1].text)
    title = a.text
    link = str(a['href'])
    id = link.split('/')[-1]
    acceptance = cols[4].text
    difficulty = cols[5].text

    algorithms.append({
                      'id': id,
                      'num': num,
                      'title': title,
                      'link': link,
                      'acceptance': acceptance,
                      'difficulty': difficulty
                      })

for algorithm in algorithms:

    browser.visit(algorithm['link'])
    question_ps = browser.find_by_css('div.question-content p')
    content = ''
    tags = []
    if len(question_ps) != 0:
        for question_p in question_ps:
            content += question_p.outer_html

        tag_as = browser.find_by_css('div#tags + span.hidebutton a')
        for tag_a in tag_as:
            tags.append(tag_a.html)

    algorithm['question'] = content
    algorithm['question_markdown'] = html2text.html2text(content)
    algorithm['tags'] = tags

    db.child("algorithms").child(algorithm['id']).set(algorithm, user['idToken'])

browser.quit()







