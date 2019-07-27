list = {"content": ["虽然我们可以由别人的学问而变的博学，无论如何，我们要由自己的智能才能成为明哲。\xa0"]}
list1 = {"ss": '人们一方面渴望爱情，另一方面却把其他的东西：如成就、地位、名利和权力看得重于爱情。\xa0'}
s = '虽然我们可以由别人的学问而变的博学，无论如何，我们要由自己的智能才能成为明哲。\xa0'

if s[-1:] == " ":
    print("yes")
print(s[-1:])
print(list["content"])

if list["content"][-1:] == " ":
    print("yes")
    list["content"] = list["content"][:-1]
    print(list["content"])
print(list["content"][-1:])

list = ['asdsad']
print(list[-1:])