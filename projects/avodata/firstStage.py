import numpy as np
import math

def factorial(n):
	return int(math.gamma(n + 1))


def nChoosek(n, k):
	return factorial(n) / (factorial(k) * factorial(n - k))


def find_c_and_a(m):
	num_of_professions = m.shape[1]
	num_of_rows = int(nChoosek(num_of_professions, 2))
	A = np.zeros(shape=(num_of_rows, num_of_professions))
	row = 0

	for i in range(num_of_professions):
		for j in range(i+1, num_of_professions):
			A[row][i] = 1
			A[row][j] = -1
			row += 1

	c = np.empty(shape = (num_of_rows, 1))
	#c_matrix = np.empty((num_of_rows, m.shape[0]))
	c_matrix = np.array([])
	for i in range(m.shape[0]):
		r = m[i].transpose()
		c = np.dot(A, m[i].transpose())
		#c_matrix = np.append(c_matrix, [c], axis=0)
		c_matrix = np.append(c_matrix, c)

	new_m = np.array([])
	errors = np.array([])
	num_of_element_in_c = c_matrix.shape[0]
	for i in range(0, num_of_element_in_c, num_of_professions):
		row_in_m = np.linalg.lstsq(A, c_matrix[i:(i+num_of_professions)], None) # TODO how can i got the error?
		new_m = np.append(new_m, row_in_m)
		#errors = np.append(errors, error)


	return new_m, A

a = np.matrix([-2,-2,4])
b, aaa = find_c_and_a(a)
print(b)
print(aaa)















